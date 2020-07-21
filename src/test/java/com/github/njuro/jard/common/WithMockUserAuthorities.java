package com.github.njuro.jard.common;

import com.github.njuro.jard.common.WithMockUserAuthorities.WithMockUserAuthoritiesSecurityContext;
import com.github.njuro.jard.user.UserAuthority;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
          new User(
              "user", "password", true, true, true, true, Arrays.asList(withAuthorities.value()));
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(
              principal, principal.getPassword(), principal.getAuthorities());
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authentication);
      return context;
    }
  }
}
