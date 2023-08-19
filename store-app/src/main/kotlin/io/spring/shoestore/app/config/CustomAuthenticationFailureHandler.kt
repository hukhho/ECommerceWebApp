package io.spring.shoestore.app.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.authentication.AuthenticationFailureHandler


class CustomAuthenticationFailureHandler : AuthenticationFailureHandler {

    private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()
    private val log = LoggerFactory.getLogger(CustomAuthenticationFailureHandler::class.java)

    override fun onAuthenticationFailure(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {

        request?.session?.setAttribute("error", "Bad credentials")

        redirectStrategy.sendRedirect(request, response, "/log-in?error=true")
    }
}