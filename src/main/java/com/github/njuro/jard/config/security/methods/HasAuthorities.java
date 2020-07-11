package com.github.njuro.jard.config.security.methods;

import com.github.njuro.jard.user.UserAuthority;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to protect endpoints. Logged in user has access to endpoint if and only if he/she
 * has <strong>all</strong> of the user authorities defined in {@link #value()}.
 *
 * @see UserAuthority
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HasAuthorities {
  UserAuthority[] value();
}
