package com.github.njuro.jard.security

import com.github.njuro.jard.*
import com.github.njuro.jard.common.Constants.JWT_COOKIE_NAME
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.config.security.JsonUsernamePasswordAuthenticationFilter
import com.github.njuro.jard.user.UserFacade
import com.github.njuro.jard.utils.validation.ValidationErrors.OBJECT_ERROR
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.OffsetDateTime

@WithContainerDatabase
internal class AuthenticationIntegrationTest : MockMvcTest() {

    @Autowired
    private lateinit var userFacade: UserFacade

    @Value("\${app.jwt.expiration:604800}")
    private val jwtExpiration = 0

    @Nested
    @DisplayName("login")
    inner class Login {
        private fun login(
            loginRequest: JsonUsernamePasswordAuthenticationFilter.LoginRequest,
            ip: String = "127.0.0.1"
        ) =
            mockMvc.post("${Mappings.API_ROOT}/login") {
                body(loginRequest)
                with { it.apply { remoteAddr = ip } }
            }

        @Test
        fun `valid login`() {
            val baseDate = OffsetDateTime.now()
            val user = userFacade.createUser(user(username = "user", password = "password").toForm())

            login(loginRequest(user.username, "password"), ip = "127.0.0.2").andExpect {
                status { isOk() }
                cookie {
                    path(JWT_COOKIE_NAME, "/")
                    maxAge(JWT_COOKIE_NAME, -1)
                    secure(JWT_COOKIE_NAME, false)
                    httpOnly(JWT_COOKIE_NAME, true)
                }
                jsonPath("$.username") { value("user") }
                jsonPath("$.role") { exists() }
                jsonPath("$.authorities") { exists() }
                jsonPath("$.email") { exists() }
                jsonPath("$.registrationIp") { doesNotExist() }
            }.andReturn().response.getHeader(HttpHeaders.SET_COOKIE).shouldContainIgnoringCase("SameSite=Strict")

            userFacade.resolveUser(user.username).should {
                it.lastLoginIp shouldBe "127.0.0.2"
                it.lastLogin shouldBeAfter baseDate
            }
        }

        @Test
        fun `valid login with remember me option`() {
            val user = userFacade.createUser(user(username = "user", password = "password").toForm())

            login(loginRequest(user.username, "password", rememberMe = true)).andExpect {
                status { isOk() }
                cookie { maxAge(JWT_COOKIE_NAME, jwtExpiration) }
            }
        }

        @Test
        fun `invalid login`() {
            val user = userFacade.createUser(user(username = "user", password = "password").toForm())

            login(loginRequest(user.username, "something")).andExpect {
                status { isUnauthorized() }
                match(validationError(OBJECT_ERROR))
            }
        }
    }

    @Test
    @WithMockJardUser
    fun logout() {
        userFacade.currentUser.shouldNotBeNull()

        mockMvc.post("${Mappings.API_ROOT}/logout") { with(csrf()) }.andExpect {
            status { isOk() }
            cookie { maxAge(JWT_COOKIE_NAME, 0) }
        }

        userFacade.currentUser.shouldBeNull()
    }

    @Test
    @WithMockJardUser
    fun `authenticated request`() {
        mockMvc.get("${Mappings.API_ROOT}/secured") { setUp() }.andExpect { status { isOk() } }
    }

    @Test
    fun `unauthenticated request`() {
        mockMvc.get("${Mappings.API_ROOT}/secured") { setUp() }.andExpect { status { isUnauthorized() } }
    }
}
