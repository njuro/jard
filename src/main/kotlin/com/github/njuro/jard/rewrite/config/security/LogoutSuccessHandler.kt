package com.github.njuro.jard.rewrite.config.security

import com.github.njuro.jard.config.security.NoRedirectStrategy
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import org.springframework.stereotype.Component

@Component
class LogoutSuccessHandler : SimpleUrlLogoutSuccessHandler() {
    init {
        redirectStrategy = NoRedirectStrategy()
    }
}