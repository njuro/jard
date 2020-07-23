package com.github.njuro.jard.common;

import com.github.njuro.jard.common.WithMockUserAuthorities.WithMockUserAuthoritiesSecurityContext;
import com.github.njuro.jard.user.User;
import com.github.njuro.jard.user.UserAuthority;
import java.lang.annotation.*;
import java.util.Arrays;
import java.util.HashSet;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * Customization of {@link WithMockUser#authorities()}, enabling to use custom {@link UserAuthority}
 * as value, instead of hardcoded strings.
 *
 * @see WithMockUser
 * @see UserAuthority
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
@WithSecurityContext(factory = WithMockUserAuthoritiesSecurityContext.class)
public @interface WithMockUserAuthorities {

  UserAuthority[] value() default {};

  class WithMockUserAuthoritiesSecurityContext
      implements WithSecurityContextFactory<WithMockUserAuthorities> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserAuthorities withAuthorities) {
      User principal =
          User.builder()
              .username("user")
              .password("password")
              .authorities(new HashSet<>(Arrays.asList(withAuthorities.value())))
              .build();
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(
              principal, principal.getPassword(), principal.getAuthorities());
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authentication);
      return context;
    }
  }
}
