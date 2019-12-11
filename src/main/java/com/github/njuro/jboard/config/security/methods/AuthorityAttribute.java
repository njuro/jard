package com.github.njuro.jboard.config.security.methods;

import com.github.njuro.jboard.user.UserAuthority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.access.ConfigAttribute;

@AllArgsConstructor
public class AuthorityAttribute implements ConfigAttribute {

  private static final long serialVersionUID = -8715812535674792037L;
  @Getter private final UserAuthority authority;

  @Override
  public String getAttribute() {
    return authority.name();
  }
}
