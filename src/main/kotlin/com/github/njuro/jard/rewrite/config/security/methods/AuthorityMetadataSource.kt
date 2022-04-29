package com.github.njuro.jard.rewrite.config.security.methods

import org.springframework.core.annotation.AnnotationUtils.findAnnotation
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource
import java.lang.reflect.Method

/**
 * Gets required authorities from [HasAuthorities] annotation and converts them to config
 * attributes.
 *
 * @see AuthorityAttribute
 */
class AuthorityMetadataSource : AbstractMethodSecurityMetadataSource() {

    override fun getAttributes(method: Method, targetClass: Class<*>): Collection<ConfigAttribute> =
        findAnnotation(method, HasAuthorities::class.java)?.value?.map(::AuthorityAttribute) ?: emptyList()

    override fun getAllConfigAttributes(): Collection<ConfigAttribute> = emptyList()

}
