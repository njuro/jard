package com.github.njuro.jard.rewrite.config.security.methods

import org.springframework.context.annotation.Configuration
import org.springframework.security.access.AccessDecisionManager
import org.springframework.security.access.method.MethodSecurityMetadataSource
import org.springframework.security.access.vote.AffirmativeBased
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration

/**
 * Registers [AuthorityVoter] as mechanism for granting access to endpoints.
 *
 * @see HasAuthorities
 */
@Configuration
@EnableGlobalMethodSecurity
class MethodSecurityConfig : GlobalMethodSecurityConfiguration() {

    override fun customMethodSecurityMetadataSource(): MethodSecurityMetadataSource = AuthorityMetadataSource()

    override fun accessDecisionManager(): AccessDecisionManager = AffirmativeBased(listOf(AuthorityVoter()))

}

