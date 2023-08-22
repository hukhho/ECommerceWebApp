package io.spring.shoestore.app.config

import io.spring.shoestore.core.users.User
import io.spring.shoestore.core.users.UserId
import io.spring.shoestore.core.users.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class AuthenticationUtils {

    @Autowired
    private lateinit var userService: UserService

    private val log = LoggerFactory.getLogger(AuthenticationUtils::class.java)

    fun getUserDetailsFromAuthentication(authentication: Authentication?): CustomUserDetails? {
        if (authentication is UsernamePasswordAuthenticationToken) {
            return authentication.principal as? CustomUserDetails
        }
        if (authentication?.principal is OidcUser) {
            log.info("authentication principal")

            val oidcUser = authentication.principal as OidcUser
            log.info("oidcUser $oidcUser")
            log.info("email ${oidcUser.email}")

            val email = oidcUser.email

            // Check if user exists in the database
            var user = userService.findByEmail(email)

            log.info("auth: $user")

            // If user doesn't exist, create a new one
            if (user == null) {
                val randomPassword = generateRandomPassword(12)
                val hashedPassword = BCrypt.hashpw(randomPassword, BCrypt.gensalt())

                user = User(
                    id = UserId(UUID.randomUUID()),
                    username = oidcUser.nickName,
                    email = email,
                    fullName = oidcUser.fullName ?: oidcUser.name,
                    password = hashedPassword,
                    roleID = "US",
                    status = true
                )

                userService.save(user)
            }

            return CustomUserDetails(
                username = user.username,
                password = user.password,
                email = user.email,
                fullName = user.fullName,
                userID = user.id,
                authorities = listOf(SimpleGrantedAuthority(user.roleID))
            )
        }
        return null
    }

    fun generateRandomPassword(length: Int): String {
        val random = SecureRandom()
        val bytes = ByteArray(length)
        random.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun updateAuthenticationWithNewDetails(newFullName: String) {
        val currentAuthentication = SecurityContextHolder.getContext().authentication
        val currentPrincipal = currentAuthentication.principal as CustomUserDetails

        currentPrincipal.setFullName(newFullName)

        val newAuthentication = UsernamePasswordAuthenticationToken(
            currentPrincipal, currentAuthentication.credentials, currentAuthentication.authorities
        )
        SecurityContextHolder.getContext().authentication = newAuthentication
    }


}
