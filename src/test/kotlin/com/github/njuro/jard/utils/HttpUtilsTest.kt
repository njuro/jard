package com.github.njuro.jard.utils

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest

internal class HttpUtilsTest {

    @Test
    fun `get client ip from requests`() {
        HttpUtils.getClientIp(
            MockHttpServletRequest().apply {
                remoteAddr = "127.0.0.1"
                addHeader("X-Forwarded-For", "unknown")
                addHeader("HTTP_VIA", "127.0.0.2,127.0.0.3")
            }
        ) shouldBe "127.0.0.2"

        HttpUtils.getClientIp(
            MockHttpServletRequest().apply {
                remoteAddr = "127.0.0.1"
                addHeader("X-Forwarded-For", "unknown")
            }
        ) shouldBe "127.0.0.1"
    }

    @Test
    fun `get origin url`() {
        HttpUtils.getOriginUrl("https://www.google.com/maps?city=Bratislava#zoom") shouldBe "https://www.google.com"
        HttpUtils.getOriginUrl("http://123.456.789/") shouldBe "http://123.456.789"
        HttpUtils.getOriginUrl("http://localhost:1234") shouldBe "http://localhost:1234"
        HttpUtils.getOriginUrl("xxx") shouldBe "xxx"
    }

    @Test
    fun `get domain from url`() {
        HttpUtils.getDomain("LOCALHOST:1234/") shouldBe "localhost"
        HttpUtils.getDomain("https://named-docker-container/path") shouldBe "localhost"
        HttpUtils.getDomain("https://www.google.com") shouldBe "google.com"
        HttpUtils.getDomain("xxx") shouldBe "xxx"
    }
}
