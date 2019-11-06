package com.github.njuro.jboard.config;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import nz.net.ultraq.thymeleaf.decorators.strategies.GroupingRespectLayoutTitleStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

/**
 * Custom Thymeleaf configuration which registers additional Thymeleaf dialect to be used, such as
 * {@link LayoutDialect} and {@link SpringSecurityDialect}
 *
 * @author njuro
 */
@Configuration
public class ThymeleafConfig {

    @Bean
    public LayoutDialect layoutDialect() {
        // grouping strategy groups same elements in <head>, such as <script> or <link> together when merging
        return new LayoutDialect(new GroupingRespectLayoutTitleStrategy());
    }

    @Bean
    public SpringSecurityDialect securityDialect() {
        return new SpringSecurityDialect();
    }

}
