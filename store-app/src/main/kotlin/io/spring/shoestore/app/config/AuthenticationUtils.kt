package io.spring.shoestore.app.config

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class AuthenticationUtils {

    fun getUserDetailsFromAuthentication(authentication: Authentication?): CustomUserDetails? {
        if (authentication is UsernamePasswordAuthenticationToken) {
            return authentication.principal as? CustomUserDetails
        }
        return null
    }
}