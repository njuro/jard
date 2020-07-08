package com.github.njuro.jard.attachment;

/**
 * Enum specifying how is the content uploaded by users (attachment) files stored. Specified by
 * application property {@code app.user.content.storage}.
 */
public enum UserContentStorageMode {
  /** User files are stored in local filesystem */
  LOCAL,
  /** User files are stored both locally and in remote Amazon S3 bucket */
  AMAZON_S3
}
