package com.github.njuro.jard.config;

import com.github.njuro.jard.utils.TemplateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * Configuration for Thymeleaf template engine, used for resolving stored HTML templates (with
 * variables).
 *
 * @see TemplateService
 */
@Configuration
public class TemplateEngineConfig {

  @Bean(name = "springTemplateEngine")
  public TemplateEngine templateEngine() {
    var engine = new SpringTemplateEngine();
    engine.addTemplateResolver(templateResolver());
    return engine;
  }

  private ITemplateResolver templateResolver() {
    var resolver = new ClassLoaderTemplateResolver();
    resolver.setPrefix("templates/");
    resolver.setSuffix(".html");
    resolver.setTemplateMode(TemplateMode.HTML);
    resolver.setOrder(1);
    resolver.setCacheable(true);
    return resolver;
  }
}
