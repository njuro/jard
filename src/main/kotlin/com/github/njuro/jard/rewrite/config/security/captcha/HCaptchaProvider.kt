package com.github.njuro.jard.rewrite.config.security.captcha

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration

@Component
class HCaptchaProvider(
    private val objectMapper: ObjectMapper,
    @Value("\${app.hcaptcha.secret:0x0000000000000000000000000000000000000000}") private val hCaptchaSecret: String?
    ) : CaptchaProvider {

    override fun verifyCaptchaToken(captchaToken: String): CaptchaVerificationResult {
        if (hCaptchaSecret.isNullOrBlank()) {
            return HCaptchaVerificationResult("Missing hCaptcha secret")
        }

        val payload = "response=$captchaToken&secret=$hCaptchaSecret"
        val request = HttpRequest.newBuilder()
            .timeout(Duration.ofSeconds(5))
            .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
            .uri(VERIFY_URL)
            .POST(BodyPublishers.ofString(payload))
            .build()

        return try {
            val response = HTTP_CLIENT.send(request, BodyHandlers.ofString())
            if (!HttpStatus.valueOf(response.statusCode()).is2xxSuccessful) {
                HCaptchaVerificationResult("CAPTCHA verification server returned error")
            } else objectMapper.readValue(response.body(), HCaptchaVerificationResult::class.java)
        } catch (ex: JsonProcessingException) {
            logger.error(ex) { "Reading response failed" }
            HCaptchaVerificationResult("Failed to read response from CAPTCHA verification server")
        } catch (ex: IOException) {
            logger.error(ex) {"Sending request failed" }
            HCaptchaVerificationResult("Failed to send CAPTCHA verification request")
        }
    }

    internal class HCaptchaVerificationResult(
        override val isVerified: Boolean,
        @JsonProperty("error-codes") override val errors: List<String> = emptyList()
    ) : CaptchaVerificationResult {
        constructor(errorMessage: String): this(isVerified = false, errors = listOf(errorMessage))
    }

    companion object: KLogging() {
        private val HTTP_CLIENT = HttpClient.newHttpClient()
        private val VERIFY_URL = URI.create("https://hcaptcha.com/siteverify")
    }
}
