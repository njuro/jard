package com.github.njuro.jard.rewrite.user.dto

/** DTO for requesting a trigger of password reset process (in case of forgotten password).  */
data class ForgotPasswordDto(
    /** [User.username]  */
    val username: String,

    /** IP the reset request came from.  */
    var ip: String,

    /** User-Agent header of reset request.  */
    var userAgent: String?,

    /** Captcha token for bot protection.  */
    val captchaToken: String
)
