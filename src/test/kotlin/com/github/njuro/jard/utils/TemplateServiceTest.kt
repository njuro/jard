package com.github.njuro.jard.utils

import com.github.njuro.jard.WithContainerDatabase
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@WithContainerDatabase
internal class TemplateServiceTest {

    @Autowired
    private lateinit var templateService: TemplateService

    @Test
    fun `test template`() {
        templateService.resolveTemplate("test_template", mapOf("testVariable" to "updatedVariable")).should {
            it shouldNotContain "originalVariable"
            it shouldContain "updatedVariable"
        }
    }

    @Test
    fun `forgot password template`() {
        val variables = mapOf(
            "username" to "root",
            "clientUrl" to "https://jard.buzz",
            "token" to "12345",
            "ip" to "1.2.3.4",
            "userAgent" to "Mozilla Firefox 66",
            "timestamp" to "03.03.2021 06:00"
        )
        val resolved = templateService.resolveTemplate("forgot_password", variables)
        resolved shouldNotContain "data-th-"
        variables.values.forEach { resolved shouldContain it }
    }

    @Test
    fun `reset password template`() {
        val variables = mapOf(
            "username" to "root",
            "clientUrl" to "https://jard.buzz",
        )
        val resolved = templateService.resolveTemplate("reset_password", variables)
        resolved shouldNotContain "data-th-"
        variables.values.forEach { resolved shouldContain it }
    }
}
