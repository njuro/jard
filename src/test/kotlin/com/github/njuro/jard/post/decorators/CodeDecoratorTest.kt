package com.github.njuro.jard.post.decorators

import com.github.njuro.jard.common.Constants
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class CodeDecoratorTest : PostDecoratorTest() {
    override fun initDecorator() = CodeDecorator()

    @ParameterizedTest
    @ValueSource(
        strings = [
            "[code]text[/code]",
            "[CODE]text[/code]",
            "[code]\nmultiple\n[/code] \n [code]code blocks[/code]"
        ]
    )
    fun `valid code block`(input: String) {
        decorate(input).shouldContainInOrder(Constants.CODE_START, Constants.CODE_END)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "[code]text",
            "[code]text[code]",
            "test[/code]",
            "[code] [/code]",
            "text"]
    )
    fun `invalid code block`(input: String) {
        decorate(input) shouldBe input
    }
}