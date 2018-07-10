package com.github.njuro.jboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;


/**
 * Entry point for the JBoard application
 *
 * @author njuro
 */
@EntityScan(
        basePackageClasses = {JBoardApplication.class, Jsr310JpaConverters.class}
)
@SpringBootApplication
public class JBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(JBoardApplication.class, args);
    }
}
