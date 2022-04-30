package com.github.njuro.jard.rewrite.user.dto

import com.github.njuro.jard.common.Constants
import com.github.njuro.jard.common.InputConstraints
import com.github.njuro.jard.rewrite.user.User
import com.github.njuro.jard.rewrite.user.UserRole
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/** Form for creating/updating a [User]  */
data class UserForm(
    /** [User.username]  */
    @Size(
        min = InputConstraints.MIN_USERNAME_LENGTH,
        max = InputConstraints.MAX_USERNAME_LENGTH,
        message = "{validation.user.username.length}"
    )
    val username: String,

    /** [User.password]  */
    @Size(
        min = InputConstraints.MIN_PASSWORD_LENGTH,
        message = "{validation.user.password.length}"
    )
    val password: String,

    /** [User.password]  */
    val passwordRepeated: String,

    /** [User.email]  */
    @Email(message = "{validation.user.email.invalid}")
    val email: String,

    /** [User.registrationIp]  */
    @Pattern(regexp = Constants.IP_PATTERN)
    var registrationIp: String,

    /** [User.role]  */
    val role: UserRole
) {
    /** Validates that [.password] and [.passwordRepeated] are equal.  */
    @AssertTrue(message = "{validation.user.password.match}")
    fun isPasswordMatching(): Boolean = password == passwordRepeated

    /** Creates [User] from values of this form and marks him/her as enabled.  */
    fun toUser(): User = User(
        username = username,
        password = password,
        email = email,
        registrationIp = registrationIp,
        role = role,
        authorities = role.defaultAuthorities,
        enabled = true
    )
}
