package com.github.njuro.jard.rewrite.config.security.captcha

/** Provider for verifying CAPTCHA response token.  */
interface CaptchaProvider {
    fun verifyCaptchaToken(captchaToken: String): CaptchaVerificationResult
}
