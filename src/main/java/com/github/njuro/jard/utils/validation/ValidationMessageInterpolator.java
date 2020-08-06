package com.github.njuro.jard.utils.validation;

import static com.github.njuro.jard.common.InputConstraints.MAX_ATTACHMENT_SIZE;

import java.util.Locale;
import javax.validation.MessageInterpolator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.context.MessageSource;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;

/** Custom interpolator of validation messages. */
public class ValidationMessageInterpolator implements MessageInterpolator {

  private final MessageInterpolator defaultInterpolator;

  public ValidationMessageInterpolator(MessageSource messageSource) {
    defaultInterpolator =
        new ResourceBundleMessageInterpolator(
            new MessageSourceResourceBundleLocator(messageSource));
  }

  @Override
  public String interpolate(String messageTemplate, Context context) {
    String message = defaultInterpolator.interpolate(messageTemplate, context);
    return processMessage(message);
  }

  @Override
  public String interpolate(String messageTemplate, Context context, Locale locale) {
    String message = defaultInterpolator.interpolate(messageTemplate, context, locale);
    return processMessage(message);
  }

  private String processMessage(String message) {
    // interpolate all custom variables
    message = message.replaceAll("\\[MAX_ATTACHMENT_SIZE]", String.valueOf(MAX_ATTACHMENT_SIZE));

    return message;
  }
}
