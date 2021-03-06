package com.github.njuro.jard.post;

import com.github.njuro.jard.attachment.Attachment;
import com.github.njuro.jard.base.BaseEntity;
import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.board.BoardSettings;
import com.github.njuro.jard.thread.Thread;
import com.github.njuro.jard.user.UserRole;
import java.time.OffsetDateTime;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;

/** Entity representing a post in thread. */
@Entity
@Indexed
@Table(name = "posts")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
@SuppressWarnings("JavadocReference")
public class Post extends BaseEntity {

  private static final long serialVersionUID = 5654328565108212395L;

  /** Number identifier of this post - generated by {@link Board} it belongs to. */
  @Basic
  @Column(nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long postNumber;

  /** (Optional) name of the poster. */
  @Basic @ToString.Include @Field private String name;

  /** (Optional) hashed password of the poster. Used to prove identity across different post. */
  @Basic @Field private String tripcode;

  /**
   * (Optional) logged in user can decide to show his/her role in his/her post (for example as proof
   * of identity)
   */
  @Enumerated(EnumType.STRING)
  private UserRole capcode;

  /** Body of the post. */
  @Basic
  @Column(columnDefinition = "TEXT")
  @AnalyzerDef(
      name = "postAnalyzer",
      charFilters = @CharFilterDef(factory = HTMLStripCharFilterFactory.class),
      tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
      filters = {
        @TokenFilterDef(factory = StandardFilterFactory.class),
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class),
        @TokenFilterDef(factory = LowerCaseFilterFactory.class),
        @TokenFilterDef(factory = StopFilterFactory.class),
        @TokenFilterDef(
            factory = SnowballPorterFilterFactory.class,
            params = @Parameter(name = "language", value = "English"))
      })
  @Field(analyzer = @Analyzer(definition = "postAnalyzer"))
  private String body;

  /** Date and time when this post was created. */
  @Column(nullable = false)
  private OffsetDateTime createdAt;

  /** IP of the poster. */
  @Basic
  @Column(nullable = false)
  private String ip;

  /**
   * (Optional) country name of poster (based on {@link #ip}). Must be enabled on board level.
   *
   * @see BoardSettings#countryFlags
   */
  @Basic private String countryCode;

  /**
   * (Optional) country name of poster (based on {@link #ip}). Must be enabled on board level.
   *
   * @see BoardSettings#countryFlags
   */
  @Basic private String countryName;

  /**
   * (Optional) unique thread ID of poster (based on {@link #ip}). Must be enabled on board level.
   *
   * @see BoardSettings#posterThreadIds
   */
  @Basic private String posterThreadId;

  /** Sage means the post will not bump the thread. Applies only to replies (not original posts). */
  @Basic private boolean sage;

  /** Secret code set by poster which enables him to delete his own post afterwards. */
  @Basic private String deletionCode;

  /** {@link Thread} this post belongs to. */
  @ManyToOne(fetch = FetchType.LAZY)
  @Fetch(FetchMode.JOIN)
  @EqualsAndHashCode.Include
  private Thread thread;

  /** (Optional) attachment uploaded with this post. */
  @OneToOne(fetch = FetchType.LAZY)
  @Fetch(FetchMode.JOIN)
  private Attachment attachment;

  /** Before inserting to database, set creation date to current date and time. */
  @PrePersist
  private void setCreatedAt() {
    createdAt = OffsetDateTime.now();
  }

  /** @return true if this post is original post in its {@link Thread}, false otherwise */
  public boolean isOriginalPost() {
    return equals(thread.getOriginalPost());
  }
}
