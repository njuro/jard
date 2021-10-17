package com.github.njuro.jard.thread;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

/** Entity representing calculated statistics for a {@link Thread}. */
@Entity
@Getter
@Immutable
@Subselect(
    """
SELECT t.id AS thread_id,
       COUNT(p.id) - 1      AS reply_count,
       COUNT(a.id)          AS attachment_count,
       COUNT(DISTINCT p.ip) AS poster_count
FROM threads t
         LEFT JOIN posts op ON t.original_post_id = op.id
         LEFT JOIN posts p ON t.id = p.thread_id
         LEFT JOIN attachments a on p.attachment_id = a.id
GROUP BY t.id
""")
@Synchronize({"threads", "posts", "attachments"})
public class ThreadStatistics implements Serializable {

  @Serial private static final long serialVersionUID = -6970015603774200061L;

  /** Id of {@link Thread} theses statistics belong to. */
  @Id @JsonIgnore private UUID threadId;

  /** How many replies this thread have (original post is not counted). */
  private int replyCount;

  /** How many attachments are in this thread (including original post). */
  private int attachmentCount;

  /** How many different IPs are active in this thread (including original poster). */
  private int posterCount;
}
