package com.github.njuro.jard.rewrite.user

import org.springframework.security.core.GrantedAuthority

/**
 * Enum representing authority, which can be granted to [com.github.njuro.jard.user.User] and enable him/her additional
 * capabilities in the system.
 *
 * @see UserRole
 *
 * @see HasAuthorities
 */
enum class UserAuthority : GrantedAuthority {
    /** Allows to create/update/delete [Board] and their settings.  */
    MANAGE_BOARDS,

    /** Allows to toggle `stickied` status of [Thread].  */
    TOGGLE_STICKY_THREAD,

    /** Allows to toggle `locked` status of [Thread].  */
    TOGGLE_LOCK_THREAD,

    /** Allows to delete [Post] or whole [Thread].  */
    DELETE_POST,

    /** Allows to create/update/delete [User].  */
    MANAGE_USERS,

    /** Allows to view IP from which the [Post] was created.  */
    VIEW_IP,

    /** Allows to create/update/delete [Ban].  */
    MANAGE_BANS,

    /** Allows access to Spring Actuator endpoints and to Spring Boot Admin interface.  */
    ACTUATOR_ACCESS;

    override fun getAuthority() = name

    companion object {
        val allAuthorities = values().toSet()
    }
}
