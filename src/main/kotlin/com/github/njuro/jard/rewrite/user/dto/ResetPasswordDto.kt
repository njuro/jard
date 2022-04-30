package com.github.njuro.jard.rewrite.user.dto

import com.github.njuro.jard.common.InputConstraints
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.Size

/** DTO for reseting user's password.  */
data class ResetPasswordDto(
    /** Secret token (obtained by user via mail)  */
    val token: String,

    /** [User.password]  */
    @Size(
        min = InputConstraints.MIN_PASSWORD_LENGTH,
        message = "{validation.user.password.length}"
    )
    val password: String,

    /** [User.password]  */
    val passwordRepeated: String
) {
    /** Validates that [.password] and [.passwordRepeated] are equal.  */
    @AssertTrue(message = "{validation.user.password.match}")
    fun isPasswordMatching(): Boolean = password == passwordRepeated
}
