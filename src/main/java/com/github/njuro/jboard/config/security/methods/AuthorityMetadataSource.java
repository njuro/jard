package com.github.njuro.jboard.config.security.methods;

import com.github.njuro.jboard.models.enums.UserAuthority;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class AuthorityMetadataSource extends AbstractMethodSecurityMetadataSource {

    @Override
    public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
        HasAuthorities annotation = AnnotationUtils.findAnnotation(method, HasAuthorities.class);
        return (annotation == null) ? Collections.emptyList() : convertToAuthorityAttributes(annotation.value());
    }

    private Collection<ConfigAttribute> convertToAuthorityAttributes(UserAuthority[] authorities) {
        return Arrays.stream(authorities).map(AuthorityAttribute::new).collect(Collectors.toSet());
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }
}
