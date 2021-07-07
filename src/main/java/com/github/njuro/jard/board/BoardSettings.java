package com.github.njuro.jard.board;

import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.config.security.captcha.CaptchaProvider;
import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.thread.Thread;
import com.github.njuro.jard.user.UserAuthority;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

/**
 * Entity representing settings of a {@link Board}.
 *
 * <p>These settings can be changed by authorized user and they determine additional constraints for
 * the board (such as how many active threads can it have, or which types of attachments can be
 * uploaded on it) or enable/disable additional features on it (such as retrieving geolocation data
 * about posters).
 *
 * @see Board
 * @see UserAuthority#MANAGE_BOARDS
 */
@Entity
@Table(name = "board_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("JavadocReference")
public class BoardSettings implements Serializable {

  private static final long serialVersionUID = 7024830970057024626L;

  /** Id of {@link Board} these settings belong to. */
  @Id private UUID boardId;

  /** {@link Board} these settings belong to. */
  @OneToOne
  @JoinColumn(name = "board_id")
  @MapsId
  private Board board;

  /**
   * Allowed attachment categories for this board.
   *
   * @see AttachmentCategory
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "board_attachment_categories",
      joinColumns = @JoinColumn(name = "board_id"))
  @Column(name = "attachment_category")
  @Enumerated(value = EnumType.STRING)
  @Builder.Default
  private Set<AttachmentCategory> attachmentCategories = new HashSet<>();

  /**
   * How many threads can be active on this board at once. If surpassed, the most stale (having
   * longest time since last bump) thread will be deleted.
   *
   * @see Thread#lastBumpAt
   */
  @ColumnDefault("100")
  private int threadLimit;

  /**
   * How many posts can be in thread on this board before new replies stop bumping it (causing it to
   * become more stale and eventually getting deleted).
   *
   * @see Thread#lastBumpAt
   */
  @ColumnDefault("300")
  private int bumpLimit;

  /**
   * If true, explicit (Not Safe For Work) content is allowed on this board.
   *
   * <p>This is just a marker value for the client and doesn't cause any additional programmatic
   * validation of the posts.
   */
  @Basic private boolean nsfw;

  /**
   * Default value for poster name on this board, which may, or may not be changed by poster.
   *
   * @see #forceDefaultPosterName
   * @see Post#name
   */
  @Basic private String defaultPosterName;

  /**
   * If true, posters on this board cannot change default value for poster name on this board.
   *
   * @see #defaultPosterName
   */
  @Basic private boolean forceDefaultPosterName;

  /**
   * If true, posts on this board will have flags indicating country of the poster (based on IP
   * address).
   *
   * @see Post#countryName
   * @see Post#countryCode
   */
  @Basic private boolean countryFlags;

  /**
   * If true, each unique IP address in thread on this board will have assigned and publicly
   * displayed pseudorandom string ID.
   *
   * <p>The poster ID is the same for given IP only in the context of given thread. This can be used
   * for example as a measure against poster spamming the thread pretending to be multiple people,
   * or to highlight all posts in thread coming from the same IP.
   *
   * @see Post#posterThreadId
   */
  @Basic private boolean posterThreadIds;

  /**
   * If true, anonymous posters will have to solve CAPTCHA before posting.
   *
   * @see CaptchaProvider
   */
  @Basic private boolean captchaEnabled;
}
