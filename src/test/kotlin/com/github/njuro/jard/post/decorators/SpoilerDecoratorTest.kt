package com.github.njuro.jard.post.decorators

import com.github.njuro.jard.common.Constants
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class SpoilerDecoratorTest : PostDecoratorTest() {
    override fun initDecorator() = SpoilerDecorator()

    @ParameterizedTest
    @ValueSource(
        strings = [
            "[spoiler]text[/spoiler]",
            "**text**",
            "[SPOILER]text[/spoiler]",
            "**multiple** \n [spoiler]\nspoilers\n[spoiler]"
        ]
    )
    fun `valid spoiler`(input: String) {
        decorate(input).shouldContainInOrder(Constants.SPOILER_START, Constants.SPOILER_END)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "[spoiler]text",
            "**text", "*****",
            "[spoiler][spoiler]",
            "**text[spoiler]",
            "**  **",
            "text"
        ]
    )
    fun `invalid spoiler`(input: String) {
        decorate(input) shouldBe input
    }
}