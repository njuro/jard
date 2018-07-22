package com.github.njuro.jboard.config;

import com.github.njuro.jboard.decorators.CrosslinkDecorator;
import com.github.njuro.jboard.decorators.Decorator;
import com.github.njuro.jboard.decorators.GreentextDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DecoratorsConfig {

    private final GreentextDecorator greentextDecorator;

    private final CrosslinkDecorator crosslinkDecorator;

    @Autowired
    public DecoratorsConfig(GreentextDecorator greentextDecorator, CrosslinkDecorator crosslinkDecorator) {
        this.greentextDecorator = greentextDecorator;
        this.crosslinkDecorator = crosslinkDecorator;
    }

    @Bean
    public List<Decorator> decorators() {
        return Arrays.asList(greentextDecorator, crosslinkDecorator);
    }
}
