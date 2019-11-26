package com.github.njuro.jboard.config.security;


import com.github.njuro.jboard.config.security.jwt.JwtAuthenticationEntryPoint;
import com.github.njuro.jboard.config.security.jwt.JwtAuthenticationFilter;
import com.github.njuro.jboard.helpers.Constants;
import com.github.njuro.jboard.helpers.Mappings;
import com.github.njuro.jboard.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Custom Spring Security configuration, which handles these processes:
 *
 * <ul>
 * <li>setup {@link UserService} as user details provider for authentication and authorization</li>
 * <li>use {@link BCryptPasswordEncoder} for securely hashing and storing passwords</li>
 * <li>use custom {@link RoleHierarchy} so that one role can be superset of others</li>
 * <li>set custom required authentication for various paths</li>
 * <li>set custom login and logout paths and register login handler</li>
 * <li>enable CSRF protection</li>
 * </ul>
 *
 * @author njuro
 * @see MethodSecurityConfig
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    private final AuthenticationSuccessHandler loginSuccessHandler;
    private final AuthenticationFailureHandler loginFailureHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(@Lazy UserService userService, @Lazy AuthenticationSuccessHandler loginSuccessHandler, AuthenticationFailureHandler loginFailureHandler, LogoutSuccessHandler logoutSuccessHandler, JwtAuthenticationFilter jwtAuthenticationFilter, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.userService = userService;
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bcryptEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/users/current").authenticated()
                .antMatchers("/auth/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler)
                .logoutUrl(Mappings.API_ROOT + "/logout")
                .deleteCookies(Constants.JWT_COOKIE_NAME)
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .cors(Customizer.withDefaults());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jsonUsernamePasswordFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordFilter() throws Exception {
        JsonUsernamePasswordAuthenticationFilter filter = new JsonUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setAuthenticationSuccessHandler(loginSuccessHandler);
        filter.setAuthenticationFailureHandler(loginFailureHandler);
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(Mappings.API_ROOT + "/login", HttpMethod.POST.name()));
        return filter;
    }

    @Bean
    public PasswordEncoder bcryptEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B, 31);
    }
}
