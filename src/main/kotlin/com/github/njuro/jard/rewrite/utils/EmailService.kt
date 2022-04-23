package com.github.njuro.jard.rewrite.utils

import com.github.njuro.jard.utils.validation.PropertyValidator
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import javax.mail.MessagingException

/**
 * Service for sending e-mail. SMTP server must be configured in order for e-mails to be sent
 * (enviroment properties SMTP_SERVER_*).
 */
@Service
class EmailService(
    @Autowired(required = false) private val mailSender: JavaMailSenderImpl?,
    private val validator: PropertyValidator,
    @Value("\${app.mail.sender:''}") private val senderAddress: String,
    @Value("\${app.mail.sender.alias:''}") private val senderAddressAlias: String
) {
    init {
        if (mailSender != null) {
            validateConnection()
            validateSenderAddress()
        }
    }

    /**
     * Sends mail to recipient with given subject and body. If SMTP server is not configured, fails
     * silently.
     *
     * @param recipient - e-mail address of recipient
     * @param subject of the mail
     * @param body of the message
     * @throws MailException if SMTP server is configured, but sending the mail failed
     */
    fun sendMail(recipient: String, subject: String, body: String) {
        if (mailSender == null) {
            logger.warn("SMTP server is not set, not-sending mail")
            return
        }
        logger.info("Sending mail to $recipient...")

        val message = mailSender.createMimeMessage()

        try {
            val alias = senderAddressAlias.takeUnless { it.isBlank() } ?: senderAddress
            MimeMessageHelper(message).apply {
                setFrom(senderAddress, alias)
                setTo(recipient)
                setSubject(subject)
                setText(body, true)
            }
        } catch (ex: MessagingException) {
            throw IllegalArgumentException("Preparation of e-mail failed", ex)
        }

        try {
            mailSender.send(message)
        } catch (ex: MailException) {
            logger.error("Failed to send email to $recipient", ex)
            throw ex
        }
    }

    private fun validateConnection() {
        try {
            if (mailSender != null) {
                logger.info("Detected SMTP server configuration, validating connection...")
                mailSender.testConnection()
                logger.info("Connection to SMTP server validated.")
            }
        } catch (ex: MessagingException) {
            throw IllegalStateException("Mail server is not available", ex)
        }
    }

    private fun validateSenderAddress() {
        validator.validateProperty(this, "senderAddress")
    }

    companion object: KLogging()
}
