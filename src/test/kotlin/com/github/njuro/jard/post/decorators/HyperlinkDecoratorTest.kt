package com.github.njuro.jard.post.decorators

import com.github.njuro.jard.common.Constants
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import org.junit.jupiter.api.Test

internal class HyperlinkDecoratorTest : PostDecoratorTest() {
    override fun initDecorator() = HyperlinkDecorator()

    @Test
    fun `valid hyperlink`() {
        decorate("text https://google.com text").shouldContainInOrder(
            Constants.HYPERLINK_START.substring(0, 3), "google.com", "google.com", Constants.HYPERLINK_END
        )
    }

    @Test
    fun `multiple hyperlinks`() {
        decorate("text https://google.com http://yahoo.com/something text").shouldContainInOrder(
            Constants.HYPERLINK_START.substring(0, 3),
            "google.com",
            "google.com",
            Constants.HYPERLINK_END,
            Constants.HYPERLINK_START.substring(0, 3),
            "yahoo.com",
            "yahoo.com",
            Constants.HYPERLINK_END
        )
    }

    @Test
    fun `invalid hyperlink`() {
        val original = "text http://google www.google.com text"
        decorate(original) shouldBe original
    }
}
