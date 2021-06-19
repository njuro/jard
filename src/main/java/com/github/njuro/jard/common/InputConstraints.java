package com.github.njuro.jard.common;

import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.board.BoardSettings;
import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.thread.Thread;
import com.github.njuro.jard.user.User;
import lombok.experimental.UtilityClass;

/** Constants defining constraints used for validation of user input. */
@SuppressWarnings({"JavadocReference", "RedundantModifiersUtilityClassLombok"})
@UtilityClass
public final class InputConstraints {

  /** Max length of {@link Board#label} (in characters). */
  public static final int MAX_BOARD_LABEL_LENGTH = 4;

  /** Max length of {@link Board#name} (in characters). */
  public static final int MAX_BOARD_NAME_LENGTH = 32;

  /** Max possible value for {@link BoardSettings#threadLimit }. */
  public static final int MAX_THREAD_LIMIT = 200;

  /** Max possible value for {@link BoardSettings#bumpLimit }. */
  public static final int MAX_BUMP_LIMIT = 1000;

  /** Max length of {@link Post#name } (in characters). */
  public static final int MAX_NAME_LENGTH = 32;

  /** Max length of {@link Post#password } (in characters). */
  public static final int MAX_TRIPCODE_PASSWORD_LENGTH = 80;

  /** Max length of {@link Thread#subject } (in characters). */
  public static final int MAX_SUBJECT_LENGTH = 255;

  /** Max length of {@link Post#body } (in characters). */
  public static final int MAX_POST_LENGTH = 1000;

  /** Max size of single poster uploaded file (in bytes). */
  public static final int MAX_ATTACHMENT_SIZE = 5_000_000;

  /** Min length of {@link User#username} (in characters). */
  public static final int MIN_USERNAME_LENGTH = 2;

  /** Max length of {@link User#username} (in characters). */
  public static final int MAX_USERNAME_LENGTH = 32;

  /** Min length of {@link User#password} (in characters). */
  public static final int MIN_PASSWORD_LENGTH = 8;

  /** Max length of {@link com.github.njuro.jard.ban.Ban#reason} (in characters). */
  public static final int MAX_BAN_REASON_LENGTH = 8;

  /**
   * Since Jackson cannot serialize static fields we have to copy all the constants into non-static
   * singleton class.
   */
  @SuppressWarnings("unused")
  public static final class Values {
    private Values() {}

    public static final Values INSTANCE = new Values();

    public final int MAX_BOARD_LABEL_LENGTH = InputConstraints.MAX_BOARD_LABEL_LENGTH;
    public final int MAX_BOARD_NAME_LENGTH = InputConstraints.MAX_BOARD_NAME_LENGTH;
    public final int MAX_THREAD_LIMIT = InputConstraints.MAX_THREAD_LIMIT;
    public final int MAX_BUMP_LIMIT = InputConstraints.MAX_BUMP_LIMIT;
    public final int MAX_NAME_LENGTH = InputConstraints.MAX_NAME_LENGTH;
    public final int MAX_TRIPCODE_PASSWORD_LENGTH = InputConstraints.MAX_TRIPCODE_PASSWORD_LENGTH;
    public final int MAX_SUBJECT_LENGTH = InputConstraints.MAX_SUBJECT_LENGTH;
    public final int MAX_POST_LENGTH = InputConstraints.MAX_POST_LENGTH;
    public final int MAX_ATTACHMENT_SIZE = InputConstraints.MAX_ATTACHMENT_SIZE;
    public final int MIN_USERNAME_LENGTH = InputConstraints.MIN_USERNAME_LENGTH;
    public final int MAX_USERNAME_LENGTH = InputConstraints.MAX_USERNAME_LENGTH;
    public final int MIN_PASSWORD_LENGTH = InputConstraints.MIN_PASSWORD_LENGTH;
    public final int MAX_BAN_REASON_LENGTH = InputConstraints.MAX_BAN_REASON_LENGTH;
  }
}
