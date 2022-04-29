package com.github.njuro.jard.rewrite.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templatemode.TemplateMode.HTML
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver

/**
 * Configuration for Thymeleaf template engine, used for resolving stored HTML templates (with
 * variables).
 *
 * @see TemplateService
 */
@Configuration
class TemplateEngineConfig {

    @Bean(name = ["springTemplateEngine"])
    fun templateEngine(): TemplateEngine =
        SpringTemplateEngine().apply {
            addTemplateResolver(templateResolver())
        }

    private fun templateResolver(): ITemplateResolver =
        ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            templateMode = HTML
            order = 1
            isCacheable = true
        }
}
