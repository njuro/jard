package com.github.njuro.jard.thread;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    "SELECT t.id AS thread_id,\n"
        + "       COUNT(p.id) - 1      AS reply_count,\n"
        + "       COUNT(a.id)          AS attachment_count,\n"
        + "       COUNT(DISTINCT p.ip) AS poster_count\n"
        + "FROM threads t\n"
        + "         LEFT JOIN posts op ON t.original_post_id = op.id\n"
        + "         LEFT JOIN posts p ON t.id = p.thread_id\n"
        + "         LEFT JOIN attachments a on p.attachment_id = a.id\n"
        + "GROUP BY t.id")
@Synchronize({"threads", "posts", "attachments"})
public class ThreadStatistics implements Serializable {

  private static final long serialVersionUID = -6970015603774200061L;

  /** Unique identifier of {@link Thread} theses statistics belong to. */
  @Id @JsonIgnore private UUID threadId;

  /** How many replies this thread have (original post is not counted) */
  private int replyCount;

  /** How many attachments are in this thread (including original post) */
  private int attachmentCount;

  /** How many different IPs are active in this thread (including original poster) */
  private int posterCount;
}
