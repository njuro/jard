package com.github.njuro.jboard.config;

import com.github.njuro.jboard.decorators.CrosslinkDecorator;
import com.github.njuro.jboard.decorators.Decorator;
import com.github.njuro.jboard.decorators.GreentextDecorator;
import com.github.njuro.jboard.decorators.SpoilerDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DecoratorsConfig {

    private final GreentextDecorator greentextDecorator;

    private final CrosslinkDecorator crosslinkDecorator;

    private final SpoilerDecorator spoilerDecorator;

    @Autowired
    public DecoratorsConfig(GreentextDecorator greentextDecorator, CrosslinkDecorator crosslinkDecorator, SpoilerDecorator spoilerDecorator) {
        this.greentextDecorator = greentextDecorator;
        this.crosslinkDecorator = crosslinkDecorator;
        this.spoilerDecorator = spoilerDecorator;
    }

    @Bean
    public List<Decorator> decorators() {
        return Arrays.asList(greentextDecorator, crosslinkDecorator, spoilerDecorator);
    }
}
