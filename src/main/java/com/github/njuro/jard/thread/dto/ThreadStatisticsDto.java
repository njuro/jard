package com.github.njuro.jard.thread.dto;

import com.github.njuro.jard.thread.ThreadStatistics;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/** DTO for {@link ThreadStatistics}. */
@SuppressWarnings("JavadocReference")
@Getter
@Setter
public class ThreadStatisticsDto implements Serializable {
  private static final long serialVersionUID = -5954407958269958234L;

  /** {@link ThreadStatistics#replyCount} */
  private int replyCount;

  /** {@link ThreadStatistics#attachmentCount} */
  private int attachmentCount;

  /** {@link ThreadStatistics#posterCount} */
  private int posterCount;
}
