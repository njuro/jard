package com.github.njuro.jard.config.security.methods;

import com.github.njuro.jard.user.UserAuthority;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HasAuthorities {
  UserAuthority[] value();
}
