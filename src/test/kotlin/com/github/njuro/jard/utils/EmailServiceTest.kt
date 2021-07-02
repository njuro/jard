package com.github.njuro.jard.utils

import com.github.njuro.jard.WithContainerDatabase
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest
@WithContainerDatabase
internal class EmailServiceTest {

    companion object {
        private const val systemEmail = "system@jard.localhost"
        private const val systemPassword = "email-password"
        private const val senderEmail = "hello@jard.localhost"
        private const val senderEmailAlias = "jard-test"

        private val mailServer = GreenMail(ServerSetup.SMTP.dynamicPort()).apply {
            setUser(systemEmail, systemPassword)
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun injectProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.mail.host") { mailServer.smtp.serverSetup.bindAddress }
            registry.add("spring.mail.port") { mailServer.smtp.serverSetup.port }
            registry.add("spring.mail.username") { systemEmail }
            registry.add("spring.mail.password") { systemPassword }
            registry.add("app.mail.sender") { senderEmail }
            registry.add("app.mail.sender.alias") { senderEmailAlias }
        }

        @AfterAll
        @JvmStatic
        fun stopServer() {
            if (mailServer.isRunning) {
                mailServer.stop()
            }
        }
    }

    @Autowired
    private lateinit var emailService: EmailService

    @Test
    fun `send e-mail`() {
        val recipientEmail = "test@jard.localhost"
        val subject = "Test subject"
        val body = "Hello from test"
        emailService.sendMail(recipientEmail, subject, body)

        val messages = mailServer.receivedMessages
        messages shouldHaveSize 1
        messages.first().should {
            it.from shouldHaveSize 1
            it.from.first().toString() shouldBe "$senderEmailAlias <$senderEmail>"
            it.allRecipients shouldHaveSize 1
            it.allRecipients.first().toString() shouldBe recipientEmail
            it.subject shouldBe subject
            it.content.toString() shouldBe "$body\r\n"
        }
    }
}
