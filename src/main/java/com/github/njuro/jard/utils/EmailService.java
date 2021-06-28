package com.github.njuro.jard.utils;

import javax.mail.MessagingException;
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

  @Value("${app.mail.sender:noreply@jard.localhost}")
  private String senderMail;

  public EmailService(@Autowired(required = false) JavaMailSenderImpl mailSender) {
    this.mailSender = mailSender;

    if (mailSender != null) {
      validateConnection();
    }
  }

  public void sendMail(String recipient, String subject, String body) {
    if (mailSender == null) {
      log.warn("SMTP server is not set, not-sending mail");
      return;
    }
    log.info("Sending mail to " + recipient + "...");

    var message = new SimpleMailMessage();
    message.setFrom(senderMail);
    message.setTo(recipient);
    message.setSubject(subject);
    message.setText(body);

    try {
      mailSender.send(message);
    } catch (MailException ex) {
      log.error("Failed to send email to " + recipient, ex);
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
}
