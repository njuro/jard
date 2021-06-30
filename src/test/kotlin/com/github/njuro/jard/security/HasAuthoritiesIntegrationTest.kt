package com.github.njuro.jard.security

import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithMockJardUser
import com.github.njuro.jard.config.security.methods.HasAuthorities
import com.github.njuro.jard.user.UserAuthority
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.stereotype.Component

@SpringBootTest
@WithContainerDatabase
class HasAuthoritiesTest {

    @Autowired
    private lateinit var securedClass: SecuredClass

    @Test
    @WithMockJardUser(UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP)
    fun `user is authenticated and has all required authorities`() {
        securedClass.securedMethod().shouldBeTrue()
    }

    @Test
    @WithMockJardUser(UserAuthority.MANAGE_BANS)
    fun `user is authenticated and has only one required authority`() {
        shouldThrow<AccessDeniedException> { securedClass.securedMethod() }
    }

    @Test
    @WithMockJardUser
    fun `user is authenticated and doesn't have any authorities`() {
        shouldThrow<AccessDeniedException> { securedClass.securedMethod() }
    }

    @Test
    fun `user is not authenticated`() {
        shouldThrow<AuthenticationCredentialsNotFoundException> { securedClass.securedMethod() }
    }
}

@Component
private open class SecuredClass {
    @HasAuthorities(UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP)
    fun securedMethod() = true
}
