package com.github.njuro.jard

import com.github.njuro.jard.user.User
import com.github.njuro.jard.user.UserAuthority
import org.springframework.core.annotation.AliasFor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.lang.annotation.Inherited

/**
 * Customization of [WithMockUser], enabling to use custom [UserAuthority]
 * as value, instead of hardcoded strings.
 *
 * @see WithMockUser
 *
 * @see UserAuthority
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS
)
@Inherited
@WithSecurityContext(factory = WithMockJardUser.WithMockJardUserSecurityContext::class)
annotation class WithMockJardUser(
    vararg val value: UserAuthority = [],
    @get:AliasFor("value") val authorities: Array<UserAuthority> = [],
    val username: String = "user"
) {
    class WithMockJardUserSecurityContext : WithSecurityContextFactory<WithMockJardUser> {
        override fun createSecurityContext(jardUser: WithMockJardUser): SecurityContext {
            val principal = User.builder()
                .username(jardUser.username)
                .password("password")
                .authorities(jardUser.value.toSet())
                .build()
            val authentication: Authentication =
                UsernamePasswordAuthenticationToken(principal, principal.password, principal.authorities)
            val context = SecurityContextHolder.createEmptyContext()
            context.authentication = authentication
            return context
        }
    }
}