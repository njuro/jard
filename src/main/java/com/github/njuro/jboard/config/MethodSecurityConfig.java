package com.github.njuro.jboard.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * Custom Spring Security configuration, which enables use of method security annotations
 * <ul>
 * <li>{@link org.springframework.security.access.annotation.Secured @Secured},</li>
 * <li>{@link org.springframework.security.access.prepost.PreAuthorize @PreAuthorize}</li>
 * <li>{@link org.springframework.security.access.prepost.PostAuthorize @PostAuthorize}</li>
 * </ul>
 * <p>
 * {@link RoleHierarchy} injected from {@link SecurityConfig} is taken into account during voting process.
 *
 * @author njuro
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final RoleHierarchy roleHierarchy;
    private final RoleVoter roleVoter;

    @Lazy
    @Autowired
    public MethodSecurityConfig(RoleHierarchy roleHierarchy, RoleVoter roleVoter) {
        this.roleHierarchy = roleHierarchy;
        this.roleVoter = roleVoter;
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
        methodSecurityExpressionHandler.setRoleHierarchy(roleHierarchy);
        return methodSecurityExpressionHandler;
    }

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        AffirmativeBased manager = (AffirmativeBased) super.accessDecisionManager();
        manager.getDecisionVoters().add(roleVoter);
        return manager;
    }
}
