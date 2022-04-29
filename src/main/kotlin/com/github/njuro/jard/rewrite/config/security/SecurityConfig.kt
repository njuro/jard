package com.github.njuro.jard.rewrite.config.security

import com.github.njuro.jard.config.security.jwt.JwtAuthenticationEntryPoint
import com.github.njuro.jard.config.security.jwt.JwtAuthenticationFilter
import com.github.njuro.jard.config.security.sba.SpringBootAdminAuthenticationFilter
import com.github.njuro.jard.rewrite.common.API_ROOT
import com.github.njuro.jard.rewrite.common.JWT_COOKIE_NAME
import com.github.njuro.jard.rewrite.user.UserAuthority.ACTUATOR_ACCESS
import com.github.njuro.jard.rewrite.user.UserRole.ADMIN
import com.github.njuro.jard.rewrite.user.dto.UserForm
import com.github.njuro.jard.rewrite.utils.getDomain
import com.github.njuro.jard.user.UserFacade
import de.codecentric.boot.admin.server.config.AdminServerProperties
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.toAnyEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpMethod.POST
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import javax.annotation.PostConstruct

/** Configuration of Spring Security.  */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userFacade: UserFacade,
    @Lazy private val loginSuccessHandler: AuthenticationSuccessHandler,
    private val loginFailureHandler: AuthenticationFailureHandler,
    private val logoutSuccessHandler: LogoutSuccessHandler,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    @Autowired(required = false) private val springBootAdminAuthenticationFilter: SpringBootAdminAuthenticationFilter?,
    private val springBootAdminProperties: AdminServerProperties,
    @Value("\${spring.boot.admin.client.enabled:true}") private val springBootAdminEnabled: Boolean,
    @Value("\${client.base.url:localhost}") private val clientBaseUrl: String,
    @Value("\${app.user.root.enabled:false}") private val rootEnabled: Boolean,
    @Value("\${app.user.root.username:root}") private val rootUsername: String,
    @Value("\${app.user.root.password:password}") private val rootPassword: String,
    @Value("\${DISABLE_CSRF_PROTECTION:false}") private val disableCsrfProtection: Boolean
) : WebSecurityConfigurerAdapter() {

    /**
     * If enabled and no users are defined in database, inserts root user with predefined credentials
     * into database.
     */
    @PostConstruct
    fun createRootUser() {
        if (!rootEnabled || userFacade.allUsers.isNotEmpty()) {
            return
        }
        logger.info { "No users in database, creating root user" }
        val root = UserForm(
            username = rootUsername,
            password = rootPassword,
            passwordRepeated = rootPassword,
            email = "",
            registrationIp = "127.0.0.1",
            role = ADMIN
        )
        userFacade.createUser(root)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userFacade).passwordEncoder(bcryptEncoder())
    }

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .requestMatchers(toAnyEndpoint())
            .hasAuthority(ACTUATOR_ACCESS.name)
            .antMatchers("$API_ROOT/secured")
            .authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
            .logout()
            .logoutSuccessHandler(logoutSuccessHandler)
            .logoutUrl("$API_ROOT/logout")
            .deleteCookies(JWT_COOKIE_NAME)
            .and()
            .cors(withDefaults())
            .csrf()
            .ignoringRequestMatchers(toAnyEndpoint())
            .csrfTokenRepository(csrfTokenRepository())
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterBefore(jsonUsernamePasswordFilter(), UsernamePasswordAuthenticationFilter::class.java)
        if (springBootAdminEnabled) {
            http.authorizeRequests()
                .antMatchers("${springBootAdminProperties.contextPath}/**")
                .hasAuthority(ACTUATOR_ACCESS.name)
                .and()
                .csrf()
                .ignoringAntMatchers("${springBootAdminProperties.contextPath}/**")
            http.addFilterBefore(
                springBootAdminAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java
            )
        }
        if (disableCsrfProtection) {
            logger.warn { "CSRF Protection disabled by enviroment variable!" }
            http.csrf().disable()
        }
        http.authorizeRequests().anyRequest().permitAll()
    }

    /**
     * Filter for users authentication check.
     *
     * @see JsonUsernamePasswordAuthenticationFilter
     */
    @Bean
    fun jsonUsernamePasswordFilter(): JsonUsernamePasswordAuthenticationFilter =
        JsonUsernamePasswordAuthenticationFilter().apply {
            setAuthenticationManager(authenticationManagerBean())
            setAuthenticationSuccessHandler(loginSuccessHandler)
            setAuthenticationFailureHandler(loginFailureHandler)
            setRequiresAuthenticationRequestMatcher(
                AntPathRequestMatcher("$API_ROOT/login", POST.name)
            )
        }

    /** Encoder for hashing user's passwords (not tripcodes).  */
    @Bean
    fun bcryptEncoder(): PasswordEncoder = BCryptPasswordEncoder(BCryptVersion.`$2B`, 31)


    /** Configures CSRF token cookie domain based on client base url.  */
    @Bean
    fun csrfTokenRepository(): CsrfTokenRepository =
        CookieCsrfTokenRepository.withHttpOnlyFalse().apply {
            setCookieDomain(getDomain(clientBaseUrl))
        }

    companion object: KLogging()
}
