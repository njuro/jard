package com.github.njuro.jboard.config.security.methods;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.Collections;


@Configuration
@EnableGlobalMethodSecurity
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return new AuthorityMetadataSource();
    }

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        return new AffirmativeBased(Collections.singletonList(new AuthorityVoter()));
    }
}
