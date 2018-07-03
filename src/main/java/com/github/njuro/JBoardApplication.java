package com.github.njuro;

import com.github.njuro.helpers.Constants;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EntityScan(
        basePackageClasses = { JBoardApplication.class, Jsr310JpaConverters.class }
)

@SpringBootApplication
public class JBoardApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(JBoardApplication.class, args);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(Constants.USER_CONTENT_URL + "**")
                .addResourceLocations(Constants.USER_CONTENT_PATH.toUri().toString());
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect(new GroupingStrategy());
    }

}
