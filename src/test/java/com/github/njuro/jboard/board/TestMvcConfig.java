package com.github.njuro.jboard.board;

import com.github.njuro.jboard.config.MvcConfig;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

@TestConfiguration
public class TestMvcConfig extends MvcConfig {

  public TestMvcConfig() {
    super(null);
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {}
}
