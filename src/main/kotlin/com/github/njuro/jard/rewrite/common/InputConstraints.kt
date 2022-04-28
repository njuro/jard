package com.github.njuro.jard.rewrite.common


/** Constants defining constraints used for validation of user input.  */
object InputConstraints {
    /** Max length of [Board.label] (in characters).  */
    const val MAX_BOARD_LABEL_LENGTH = 4

    /** Max length of [Board.name] (in characters).  */
    const val MAX_BOARD_NAME_LENGTH = 32

    /** Max possible value for [BoardSettings.threadLimit].  */
    const val MAX_THREAD_LIMIT = 200

    /** Max possible value for [BoardSettings.bumpLimit].  */
    const val MAX_BUMP_LIMIT = 1000

    /** Max length of [Post.name] (in characters).  */
    const val MAX_NAME_LENGTH = 32

    /** Max length of [Post.password] (in characters).  */
    const val MAX_TRIPCODE_PASSWORD_LENGTH = 80

    /** Max length of [Thread.subject] (in characters).  */
    const val MAX_SUBJECT_LENGTH = 255

    /** Max length of [Post.body] (in characters).  */
    const val MAX_POST_LENGTH = 1000

    /** Max size of single poster uploaded file (in bytes).  */
    const val MAX_ATTACHMENT_SIZE = 5000000

    /** Min length of [User.username] (in characters).  */
    const val MIN_USERNAME_LENGTH = 2

    /** Max length of [User.username] (in characters).  */
    const val MAX_USERNAME_LENGTH = 32

    /** Min length of [User.password] (in characters).  */
    const val MIN_PASSWORD_LENGTH = 8

    /** Max length of [com.github.njuro.jard.ban.Ban.reason] (in characters).  */
    const val MAX_BAN_REASON_LENGTH = 1000

    /**
     * Since Jackson cannot serialize static fields we have to copy all the constants into non-static
     * singleton class.
     */
    object Values {
        val MAX_BOARD_LABEL_LENGTH = InputConstraints.MAX_BOARD_LABEL_LENGTH
        val MAX_BOARD_NAME_LENGTH = InputConstraints.MAX_BOARD_NAME_LENGTH
        val MAX_THREAD_LIMIT = InputConstraints.MAX_THREAD_LIMIT
        val MAX_BUMP_LIMIT = InputConstraints.MAX_BUMP_LIMIT
        val MAX_NAME_LENGTH = InputConstraints.MAX_NAME_LENGTH
        val MAX_TRIPCODE_PASSWORD_LENGTH = InputConstraints.MAX_TRIPCODE_PASSWORD_LENGTH
        val MAX_SUBJECT_LENGTH = InputConstraints.MAX_SUBJECT_LENGTH
        val MAX_POST_LENGTH = InputConstraints.MAX_POST_LENGTH
        val MAX_ATTACHMENT_SIZE = InputConstraints.MAX_ATTACHMENT_SIZE
        val MIN_USERNAME_LENGTH = InputConstraints.MIN_USERNAME_LENGTH
        val MAX_USERNAME_LENGTH = InputConstraints.MAX_USERNAME_LENGTH
        val MIN_PASSWORD_LENGTH = InputConstraints.MIN_PASSWORD_LENGTH
        val MAX_BAN_REASON_LENGTH = InputConstraints.MAX_BAN_REASON_LENGTH
    }
}
