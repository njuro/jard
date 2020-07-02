package com.github.njuro.jboard.config.security;

import com.github.njuro.jboard.common.Constants;
import com.github.njuro.jboard.common.Mappings;
import com.github.njuro.jboard.config.security.jwt.JwtAuthenticationEntryPoint;
import com.github.njuro.jboard.config.security.jwt.JwtAuthenticationFilter;
import com.github.njuro.jboard.user.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserFacade userFacade;

  private final AuthenticationSuccessHandler loginSuccessHandler;
  private final AuthenticationFailureHandler loginFailureHandler;
  private final LogoutSuccessHandler logoutSuccessHandler;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Autowired
  public SecurityConfig(
      UserFacade userFacade,
      @Lazy AuthenticationSuccessHandler loginSuccessHandler,
      AuthenticationFailureHandler loginFailureHandler,
      LogoutSuccessHandler logoutSuccessHandler,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
    this.userFacade = userFacade;
    this.loginSuccessHandler = loginSuccessHandler;
    this.loginFailureHandler = loginFailureHandler;
    this.logoutSuccessHandler = logoutSuccessHandler;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userFacade).passwordEncoder(bcryptEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers(Mappings.API_ROOT_USERS + "/current")
        .authenticated()
        .anyRequest()
        .permitAll()
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
        .cors(Customizer.withDefaults())
        .csrf()
        .disable(); // TODO enable when custom domain

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(jsonUsernamePasswordFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordFilter() throws Exception {
    JsonUsernamePasswordAuthenticationFilter filter =
        new JsonUsernamePasswordAuthenticationFilter();
    filter.setAuthenticationManager(authenticationManagerBean());
    filter.setAuthenticationSuccessHandler(loginSuccessHandler);
    filter.setAuthenticationFailureHandler(loginFailureHandler);
    filter.setRequiresAuthenticationRequestMatcher(
        new AntPathRequestMatcher(Mappings.API_ROOT + "/login", HttpMethod.POST.name()));
    return filter;
  }

  @Bean
  public PasswordEncoder bcryptEncoder() {
    return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B, 31);
  }
}
