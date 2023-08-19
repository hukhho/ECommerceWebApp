package io.spring.shoestore.app.config

import io.spring.shoestore.core.users.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeRequests()
            .requestMatchers("/css/**").permitAll()
            .requestMatchers("/user/**").hasAuthority("US")
            .requestMatchers("/admin/**").hasAuthority("AD")
            .and()
            .formLogin().loginPage("/log-in")
            .successHandler(customAuthenticationSuccessHandler())
            .failureHandler(customAuthenticationFailureHandler())

        http.exceptionHandling()
            .accessDeniedPage("/error")
        return http.build()
    }
    @Bean
    fun customAuthenticationSuccessHandler(): CustomAuthenticationSuccessHandler {
        return CustomAuthenticationSuccessHandler()
    }
    @Bean
    fun customAuthenticationFailureHandler(): CustomAuthenticationFailureHandler {
        return CustomAuthenticationFailureHandler()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder() // Use the appropriate password encoder
    }

    @Bean
    fun userDetailsService(userRepository: UserRepository, passwordEncoder: PasswordEncoder): UserDetailsServiceImpl {
        return UserDetailsServiceImpl(userRepository, passwordEncoder)
    }

}