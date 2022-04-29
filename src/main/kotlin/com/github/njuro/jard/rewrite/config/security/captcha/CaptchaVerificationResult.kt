package com.github.njuro.jard.rewrite.config.security.captcha

/**
 * Result of verification of CAPTCHA token.
 *
 * @see CaptchaProvider
 */
interface CaptchaVerificationResult {
    /** @return true if CAPTCHA token was successfully verified, false otherwise
     */
    val isVerified: Boolean

    /** @return list of errors in case verification of token failed
     */
    val errors: List<String>
}
