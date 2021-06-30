package com.github.njuro.jard.security

import com.github.njuro.jard.MockMvcTest
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.common.Constants.SBA_SECRET_HEADER
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.get

@WithContainerDatabase
@TestPropertySource(
    properties = [
        "management.endpoints.web.base-path=/actuator",
        "spring.boot.admin.client.enabled=true",
        "spring.boot.admin.client.url=localhost",
        "spring.boot.admin.context-path=/sba",
        "app.sba.secret=verysecretsbakey",
    ]
)
internal class SpringBootAdminAuthenticationTest : MockMvcTest() {

    @Value("\${app.sba.secret}")
    private val sbaSecret: String = ""

    @Value("\${spring.boot.admin.context-path}")
    private val sbaContextPath: String = ""

    @Test
    fun `request to SBA with valid secret`() {
        mockMvc.get(sbaContextPath) { with(csrf()); header(SBA_SECRET_HEADER, sbaSecret) }
            .andExpect { status { isOk() } }
    }

    @Test
    fun `request to SBA with invalid secret`() {
        mockMvc.get(sbaContextPath) { with(csrf()); header(SBA_SECRET_HEADER, "xxx") }
            .andExpect { status { isUnauthorized() } }
    }
}
