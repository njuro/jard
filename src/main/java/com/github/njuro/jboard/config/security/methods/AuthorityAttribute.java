package com.github.njuro.jboard.config.security.methods;

import com.github.njuro.jboard.models.enums.UserAuthority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.access.ConfigAttribute;

@AllArgsConstructor
public class AuthorityAttribute implements ConfigAttribute {

    @Getter
    private final UserAuthority authority;

    @Override
    public String getAttribute() {
        return authority.name();
    }
}
