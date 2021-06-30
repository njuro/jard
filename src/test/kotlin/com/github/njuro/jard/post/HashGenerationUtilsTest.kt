package com.github.njuro.jard.post

import com.github.njuro.jard.common.Constants.TRIPCODE_LENGTH
import com.github.njuro.jard.common.Constants.TRIPCODE_SEPARATOR
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.Test
import java.util.*

internal class HashGenerationUtilsTest {

    @Test
    fun `generate tripcode`() {
        HashGenerationUtils.generateTripcode("").shouldBeNull()
        val tripcode = HashGenerationUtils.generateTripcode("password")
            .shouldStartWith(TRIPCODE_SEPARATOR)
            .shouldHaveLength(TRIPCODE_LENGTH + 1)
        HashGenerationUtils.generateTripcode("password") shouldBe tripcode
    }

    @Test
    fun `generate poster thread id`() {
        val ip1 = "127.0.0.1"
        val ip2 = "127.0.0.2"
        val threadId1 = UUID.fromString("90780e91-999e-4711-938a-6fb67fdd0981")
        val threadId2 = UUID.fromString("bfdf1881-4cd4-4351-b52d-8d339e5eb273")

        val posterId = HashGenerationUtils.generatePosterThreadId(ip1, threadId1)
            .shouldHaveLength(8)
        HashGenerationUtils.generatePosterThreadId(ip1, threadId1) shouldBe posterId
        HashGenerationUtils.generatePosterThreadId(ip1, threadId2) shouldNotBe posterId
        HashGenerationUtils.generatePosterThreadId(ip2, threadId1) shouldNotBe posterId
    }
}
