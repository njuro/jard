package com.github.njuro.jard.rewrite.utils

import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class TemplateService(private val templateEngine: TemplateEngine) {
    /**
     * Resolves template with given name and variables.
     *
     * @param template - name of the template (located in resources/templates - HTML file)
     * @param variables - map of template variables
     * @return resolved template
     */
    fun resolveTemplate(template: String, variables: Map<String, Any?>): String {
        val context = Context().apply { setVariables(variables) }
        return templateEngine.process(template, context)
    }
}
