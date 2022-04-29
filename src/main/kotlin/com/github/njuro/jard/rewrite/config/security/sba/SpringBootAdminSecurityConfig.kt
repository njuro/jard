package com.github.njuro.jard.rewrite.config.security.sba

import com.github.njuro.jard.rewrite.common.SBA_SECRET_HEADER
import de.codecentric.boot.admin.client.config.ClientProperties
import de.codecentric.boot.admin.client.registration.BlockingRegistrationClient
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON

/** Configuration of authentication between Spring Boot Admin server and client instances.  */
@Configuration
@ConditionalOnProperty(name = ["spring.boot.admin.client.enabled"], havingValue = "true", matchIfMissing = true)
class SpringBootAdminSecurityConfig(
    /** Properties of Spring Boot Admin client.  */
    private val clientProperties: ClientProperties,

    /** Spring Boot Admin secret (used for authentication).  */
    @Value("\${app.sba.secret}")
    private val sbaSecret: String
) {

    /** Adds header with Spring Boot Admin secret to all requests from SBA server to client.  */
    @Bean
    fun httpHeadersProvider() = HttpHeadersProvider { HttpHeaders().apply { add(SBA_SECRET_HEADER, sbaSecret) } }


    /**
     * Adds header with Spring Boot Admin secret to registration request from SBA client to server.
     */
    @Bean
    fun registrationClient(): BlockingRegistrationClient {
        val template = RestTemplateBuilder()
            .setConnectTimeout(clientProperties.connectTimeout)
            .setReadTimeout(clientProperties.readTimeout)
            .build()

        return object : BlockingRegistrationClient(template) {
            override fun createRequestHeaders(): HttpHeaders {
                val headers = HttpHeaders().apply {
                    contentType = APPLICATION_JSON,
                    accept = listOf(APPLICATION_JSON)
                    add(SBA_SECRET_HEADER, sbaSecret)
                }
                return HttpHeaders.readOnlyHttpHeaders(headers)
            }
        }
    }
}
