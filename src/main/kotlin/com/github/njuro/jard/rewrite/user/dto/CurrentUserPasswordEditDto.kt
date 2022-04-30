package com.github.njuro.jard.rewrite.user.dto

import com.github.njuro.jard.common.InputConstraints
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.Size

/** DTO for changing current user's password  */
data class CurrentUserPasswordEditDto(
    /** [User.password]  */
    val currentPassword: String,

    /** [User.password]  */
    @Size(
        min = InputConstraints.MIN_PASSWORD_LENGTH,
        message = "{validation.user.password.length}"
    )
    val newPassword: String,

    /** [User.password]  */
    val newPasswordRepeated: String
) {
    /** Validates that [.newPassword] and [.newPasswordRepeated] are equal.  */
    @AssertTrue(message = "{validation.user.password.match}")
    fun isPasswordMatching(): Boolean = newPassword == newPasswordRepeated
}
