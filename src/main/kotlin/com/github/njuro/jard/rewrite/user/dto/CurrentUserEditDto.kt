package com.github.njuro.jard.rewrite.user.dto

import javax.validation.constraints.Email

/** DTO for editing of current user information.  */
data class CurrentUserEditDto(
    /** [User.email]  */
    @Email
    val email: String
)
