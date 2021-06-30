package com.github.njuro.jard.security.captcha

import com.github.njuro.jard.config.security.captcha.CaptchaVerificationResult

internal class MockCaptchaVerificationResult private constructor(private val verified: Boolean) :
    CaptchaVerificationResult {

    companion object {
        val VALID = MockCaptchaVerificationResult(true)
        val INVALID = MockCaptchaVerificationResult(false)
    }

    override fun isVerified() = verified

    override fun getErrors() = mutableListOf<String>()
}
