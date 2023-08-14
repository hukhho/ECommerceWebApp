package io.spring.shoestore.app.config

import io.spring.shoestore.core.users.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeRequests()
            .requestMatchers("/css/**").permitAll()
            .requestMatchers("/user/**").hasAuthority("ROLE_USER")
            .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
            .and()
            .formLogin().loginPage("/log-in")
        return http.build()
    }

//    @Bean
//    fun userDetailsService(): UserDetailsService {
//        val userDetails = User.withDefaultPasswordEncoder()
//            .username("user")
//            .password("password")
//            .roles("USER")
//            .build()
//        return InMemoryUserDetailsManager(userDetails)
//    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder() // Use the appropriate password encoder
    }

    @Bean
    fun userDetailsService(userRepository: UserRepository, passwordEncoder: PasswordEncoder): UserDetailsServiceImpl {
        return UserDetailsServiceImpl(userRepository, passwordEncoder)
    }

}