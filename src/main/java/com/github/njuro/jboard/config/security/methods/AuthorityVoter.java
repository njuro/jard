package com.github.njuro.jboard.config.security.methods;

import com.github.njuro.jboard.models.enums.UserAuthority;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

public class AuthorityVoter implements AccessDecisionVoter<MethodInvocation> {

  @Override
  public boolean supports(final ConfigAttribute attribute) {
    return attribute instanceof AuthorityAttribute;
  }

  @Override
  public boolean supports(final Class<?> clazz) {
    return MethodInvocation.class.isAssignableFrom(clazz);
  }

  @Override
  public int vote(
      final Authentication authentication,
      final MethodInvocation object,
      final Collection<ConfigAttribute> attributes) {
    final Set<UserAuthority> requiredAuthorities =
        attributes.stream()
            .filter(att -> att instanceof AuthorityAttribute)
            .map(AuthorityAttribute.class::cast)
            .map(AuthorityAttribute::getAuthority)
            .collect(Collectors.toSet());

    if (requiredAuthorities.isEmpty()) {
      return AccessDecisionVoter.ACCESS_ABSTAIN;
    }

    return authentication.getAuthorities().containsAll(requiredAuthorities)
        ? ACCESS_GRANTED
        : ACCESS_DENIED;
  }
}
