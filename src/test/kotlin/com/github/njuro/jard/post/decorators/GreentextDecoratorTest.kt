package com.github.njuro.jard.post.decorators

import com.github.njuro.jard.common.Constants
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class GreentextDecoratorTest : PostDecoratorTest() {
    override fun initDecorator() = GreentextDecorator()

    @ParameterizedTest
    @ValueSource(
        strings = [
            ">text",
            ">  text",
            "    >longer text > another",
            "  > some longer text",
            ">>>multiple quotes",
            ">multiline\r\n>text"
        ]
    )
    fun `valid greentext`(input: String) {
        decorate(input).shouldContainInOrder(Constants.GREENTEXT_START, Constants.GREENTEXT_END)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "aaa>text",
            "    aaaa > text",
            " aaa>text "
        ]
    )
    fun `invalid greentext`(input: String) {
        decorate(input) shouldBe input
    }
}