package io.spring.shoestore.app.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.authentication.AuthenticationSuccessHandler


class CustomAuthenticationSuccessHandler : AuthenticationSuccessHandler {

    private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()
    private val log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler::class.java)

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val authorities = authentication?.authorities

        var targetUrl = "/"
        authorities?.forEach { authority ->
            when {

                authority.authority.contains("AD") -> {
                    log.info("User has AD authority. Redirecting to /admin/dashboard.")
                    targetUrl = "/admin/dashboard"
                }
                authority.authority.contains("US") -> {
                    log.info("User has US authority. Redirecting to /products.")
                    targetUrl = "/products"
                }
                else -> {
                    log.info("User has no specific authority. Redirecting to /products.")
                    log.info(authority.authority)

                    targetUrl = "/products"
                }
            }
        }

        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}
