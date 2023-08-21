package io.spring.shoestore.app.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.io.IOException

@Controller
class LogoutHandler @Autowired constructor(
    private val clientRegistrationRepository: ClientRegistrationRepository
) : SecurityContextLogoutHandler() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun logout(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        authentication: Authentication?
    ) {
        // Invalidate the session and clear the security context
        super.logout(httpServletRequest, httpServletResponse, authentication)

        // Use the passed HttpServletRequest to build the URI
        val returnTo = ServletUriComponentsBuilder.fromRequest(httpServletRequest).build().toString()

        // Build the URL to log the user out of Auth0 and redirect them to the home page.
        val issuer = clientRegistration.providerDetails.configurationMetadata["issuer"] as String?
        val clientId = clientRegistration.clientId
        val logoutUrl = UriComponentsBuilder
                .fromHttpUrl("$issuer/v2/logout?client_id={clientId}&returnTo=http://localhost:3000")
            .encode()
            .buildAndExpand(clientId, returnTo)
            .toUriString()

        log.info("Will attempt to redirect to logout URL: {}", logoutUrl)
        try {
            httpServletResponse.sendRedirect(logoutUrl)
        } catch (ioe: IOException) {
            log.error("Error redirecting to logout URL", ioe)
        }
    }

    private val clientRegistration: ClientRegistration
        get() = clientRegistrationRepository.findByRegistrationId("auth0")
}
