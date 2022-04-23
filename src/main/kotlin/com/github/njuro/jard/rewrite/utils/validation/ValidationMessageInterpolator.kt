package com.github.njuro.jard.rewrite.utils.validation

import com.github.njuro.jard.common.InputConstraints
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator
import org.springframework.context.MessageSource
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator
import java.util.Locale
import javax.validation.MessageInterpolator
import javax.validation.MessageInterpolator.Context

/** Custom interpolator of validation messages.  */
class ValidationMessageInterpolator(messageSource: MessageSource) : MessageInterpolator {

    private val defaultInterpolator: MessageInterpolator

    init {
        defaultInterpolator = ResourceBundleMessageInterpolator(
            MessageSourceResourceBundleLocator(messageSource)
        )
    }

    override fun interpolate(messageTemplate: String, context: Context): String {
        val message = defaultInterpolator.interpolate(messageTemplate, context)
        return processMessage(message)
    }

    override fun interpolate(messageTemplate: String, context: Context, locale: Locale): String {
        val message = defaultInterpolator.interpolate(messageTemplate, context, locale)
        return processMessage(message)
    }

    private fun processMessage(message: String): String =
        // interpolate all custom variables
        message.replace("[MAX_ATTACHMENT_SIZE]", InputConstraints.MAX_ATTACHMENT_SIZE.toString())
}
