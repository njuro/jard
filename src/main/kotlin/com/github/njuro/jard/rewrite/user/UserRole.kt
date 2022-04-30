package com.github.njuro.jard.rewrite.user

import com.github.njuro.jard.rewrite.user.UserAuthority.Companion.allAuthorities
import com.github.njuro.jard.rewrite.user.UserAuthority.DELETE_POST
import com.github.njuro.jard.rewrite.user.UserAuthority.MANAGE_BANS
import com.github.njuro.jard.rewrite.user.UserAuthority.TOGGLE_LOCK_THREAD
import com.github.njuro.jard.rewrite.user.UserAuthority.TOGGLE_STICKY_THREAD
import com.github.njuro.jard.rewrite.user.UserAuthority.VIEW_IP

/**
 * Enum representing active role of a [User]. Role is a wrapper for group of user authorities.
 *
 * @see UserAuthority
 */
enum class UserRole(
    /** Default authorities for this role.  */
    val defaultAuthorities: Set<UserAuthority>
) {
    USER,
    JANITOR(DELETE_POST),
    MODERATOR(DELETE_POST, TOGGLE_LOCK_THREAD, TOGGLE_STICKY_THREAD, VIEW_IP, MANAGE_BANS),
    ADMIN(allAuthorities);

    constructor(vararg authorities: UserAuthority): this(authorities.toSet())
}
