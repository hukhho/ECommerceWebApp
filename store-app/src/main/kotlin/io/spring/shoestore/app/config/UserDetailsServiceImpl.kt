package io.spring.shoestore.app.config

import io.spring.shoestore.core.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    private val logger = LoggerFactory.getLogger(UserDetailsServiceImpl::class.java)

    override fun loadUserByUsername(username: String): UserDetails {
        logger.debug("Loading user by username: {}", username)
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found: $username")

        return CustomUserDetails(
            username = user.username,
            password = user.password,
            email = user.email,
            fullName = user.fullName,
            userID = user.id,
            authorities = listOf(SimpleGrantedAuthority(user.roleID.toString()))
        )
    }
}
