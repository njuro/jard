package com.github.njuro.jboard.config.security.methods;

import com.github.njuro.jboard.models.enums.UserAuthority;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthorityVoter implements AccessDecisionVoter<MethodInvocation> {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute instanceof AuthorityAttribute;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, MethodInvocation object, Collection<ConfigAttribute> attributes) {
        Set<UserAuthority> requiredAuthorities = attributes.stream()
                .filter(att -> att instanceof AuthorityAttribute)
                .map(AuthorityAttribute.class::cast)
                .map(AuthorityAttribute::getAuthority)
                .collect(Collectors.toSet());

        if (requiredAuthorities.isEmpty()) {
            return AccessDecisionVoter.ACCESS_ABSTAIN;
        }

        return authentication.getAuthorities().containsAll(requiredAuthorities) ? ACCESS_GRANTED : ACCESS_DENIED;
    }
}
