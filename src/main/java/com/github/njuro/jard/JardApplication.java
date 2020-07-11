package com.github.njuro.jard;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

/**
 * Entry point for the jard application
 *
 * @author njuro
 */
@EntityScan(basePackageClasses = {JardApplication.class, Jsr310JpaConverters.class})
@SpringBootApplication
public class JardApplication {

  public static void main(String[] args) {
    SpringApplication.run(JardApplication.class, args);
  }

  @PostConstruct
  public void init() {
    // set default timezone to UTC
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
}
