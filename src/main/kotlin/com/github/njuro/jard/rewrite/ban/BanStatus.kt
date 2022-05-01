package com.github.njuro.jard.rewrite.ban

/** Enum representing current status of a [Ban].  */
enum class BanStatus {
    /** Active ban - IP cannot post.  */
    ACTIVE,

    /** Expired ban - IP can post again.  */
    EXPIRED,

    /** Ban invalidated by user - IP can post again.  */
    UNBANNED,

    /** Ban is just a warning - IP can post, but will be warned.  */
    WARNING
}