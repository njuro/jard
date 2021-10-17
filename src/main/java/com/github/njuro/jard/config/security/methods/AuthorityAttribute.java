package com.github.njuro.jard.config.security.methods;

import com.github.njuro.jard.user.UserAuthority;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.access.ConfigAttribute;

/** Configuration attribute based on {@link UserAuthority}. */
@AllArgsConstructor
public class AuthorityAttribute implements ConfigAttribute {

  @Serial private static final long serialVersionUID = -8715812535674792037L;

  @Getter private final UserAuthority authority;

  @Override
  public String getAttribute() {
    return authority.name();
  }
}
