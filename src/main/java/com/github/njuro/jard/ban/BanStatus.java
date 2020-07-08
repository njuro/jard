package com.github.njuro.jard.ban;

/** Enum representing current status of a {@link Ban}. */
public enum BanStatus {

  /** Active ban - IP cannot post. */
  ACTIVE,
  /** Expired ban - IP can post again. */
  EXPIRED,
  /** Ban invalidated by user - IP can post again. */
  UNBANNED,
  /** Ban is just a warning - IP can post, but will be warned. */
  WARNING
}
