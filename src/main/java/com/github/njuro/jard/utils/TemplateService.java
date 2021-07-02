package com.github.njuro.jard.utils;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class TemplateService {

  private final TemplateEngine templateEngine;

  public TemplateService(TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  /**
   * Resolves template with given name and variables.
   *
   * @param template - name of the template (located in resources/templates - HTML file)
   * @param variables - map of template variables
   * @return resolved template
   */
  public String resolveTemplate(String template, Map<String, Object> variables) {
    var context = new Context();
    context.setVariables(variables);
    return templateEngine.process(template, context);
  }
}
