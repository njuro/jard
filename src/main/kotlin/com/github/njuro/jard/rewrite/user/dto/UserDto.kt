package com.github.njuro.jard.rewrite.user.dto

import com.fasterxml.jackson.annotation.JsonView
import com.github.njuro.jard.rewrite.base.BaseDto
import com.github.njuro.jard.rewrite.user.UserAuthority
import com.github.njuro.jard.rewrite.user.UserRole
import lombok.EqualsAndHashCode
import lombok.ToString
import java.time.OffsetDateTime
import java.util.UUID

/** DTO for [User].  */
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
class UserDto(
    id: UUID,

    /** [User.username]  */
    @JsonView(PublicView::class)
    @EqualsAndHashCode.Include
    @ToString.Include
    val username: String,

    /** [User.email]  */
    @JsonView(PublicView::class)
    @ToString.Include
    val email: String,

    /** [User.enabled]  */
    val enabled: Boolean,

    /** [User.role]  */
    @JsonView(PublicView::class)
    @ToString.Include
    private val role: UserRole,

    /** [User.authorities]  */
    @JsonView(PublicView::class)
    val authorities: Set<UserAuthority>,

    /** [User.registrationIp]  */
    val registrationIp: String,

    /** [User.lastLoginIp]  */
    val lastLoginIp: String?,

    /** [User.lastLogin]  */
    val lastLogin: OffsetDateTime?,

    /** [User.createdAt]  */
    val createdAt: OffsetDateTime
) : BaseDto(id) {
    interface PublicView
}
