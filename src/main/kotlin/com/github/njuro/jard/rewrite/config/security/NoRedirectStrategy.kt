package com.github.njuro.jard.rewrite.config.security

import org.springframework.security.web.RedirectStrategy
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/** No-op redirect strategy.  */
class NoRedirectStrategy : RedirectStrategy {
    override fun sendRedirect(request: HttpServletRequest, response: HttpServletResponse, url: String) {
        // no redirect
    }
}