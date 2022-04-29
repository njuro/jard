package com.github.njuro.jard.rewrite.config.security.methods

import org.aopalliance.intercept.MethodInvocation
import org.springframework.security.access.AccessDecisionVoter
import org.springframework.security.access.AccessDecisionVoter.ACCESS_ABSTAIN
import org.springframework.security.access.AccessDecisionVoter.ACCESS_DENIED
import org.springframework.security.access.AccessDecisionVoter.ACCESS_GRANTED
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.core.Authentication
import kotlin.reflect.full.isSuperclassOf

/**
 * Grants or denies access to endpoint based on user authority config attributes.
 *
 * @see AuthorityAttribute
 *
 * @see HasAuthorities
 */
class AuthorityVoter : AccessDecisionVoter<MethodInvocation> {
    override fun supports(attribute: ConfigAttribute): Boolean = attribute is AuthorityAttribute

    override fun supports(clazz: Class<*>): Boolean = MethodInvocation::class.isSuperclassOf(clazz.kotlin)

    override fun vote(
        authentication: Authentication,
        invocation: MethodInvocation,
        attributes: Collection<ConfigAttribute>
    ): Int {
        val requiredAuthorities = attributes.filterIsInstance<AuthorityAttribute>().map { it.authority.authority }
        if (requiredAuthorities.isEmpty()) { return ACCESS_ABSTAIN }

        val isAuthorized: Boolean = authentication.authorities.map { it.authority }.containsAll(requiredAuthorities)
        return if (isAuthorized) ACCESS_GRANTED else ACCESS_DENIED
    }
}
