package io.spring.shoestore.app.config

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import io.spring.shoestore.core.users.UserRepository
import org.slf4j.LoggerFactory

class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    private val logger = LoggerFactory.getLogger(UserDetailsServiceImpl::class.java)

    override fun loadUserByUsername(username: String): UserDetails {
        logger.debug("Loading user by username: {}", username)
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found: $username")

        return User.builder()
            .username(user.username)
            .password(user.password)
            .authorities(user.roleID)
            .build()
    }
}
