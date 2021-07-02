package com.github.njuro.jard.utils;

import com.github.njuro.jard.utils.validation.PropertyValidator;
import javax.mail.MessagingException;
import javax.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

/**
 * Service for sending e-mail. SMTP server must be configured in order for e-mails to be sent
 * (enviroment properties SMTP_SERVER_*).
 */
@Service
@Slf4j
public class EmailService {

  private final JavaMailSenderImpl mailSender;
  private final PropertyValidator validator;
  @Email private final String senderAddress;

  public EmailService(
      @Autowired(required = false) JavaMailSenderImpl mailSender,
      PropertyValidator validator,
      @Value("${app.mail.sender:''}") String senderAddress) {
    this.mailSender = mailSender;
    this.validator = validator;
    this.senderAddress = senderAddress;

    if (mailSender != null) {
      validateConnection();
      validateSenderAddress();
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
  public void sendMail(String recipient, String subject, String body) {
    if (mailSender == null) {
      log.warn("SMTP server is not set, not-sending mail");
      return;
    }
    log.info("Sending mail to " + recipient + "...");

    var message = new SimpleMailMessage();
    message.setFrom(senderAddress);
    message.setTo(recipient);
    message.setSubject(subject);
    message.setText(body);

    try {
      mailSender.send(message);
    } catch (MailException ex) {
      log.error("Failed to send email to " + recipient, ex);
      throw ex;
    }
  }

  private void validateConnection() {
    try {
      if (mailSender != null) {
        log.info("Detected SMTP server configuration, validating connection...");
        mailSender.testConnection();
        log.info("Connection to SMTP server validated.");
      }
    } catch (MessagingException ex) {
      throw new IllegalStateException("Mail server is not available", ex);
    }
  }

  private void validateSenderAddress() {
    validator.validateProperty(this, "senderAddress");
  }
}
