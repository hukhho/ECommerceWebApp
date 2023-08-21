package io.spring.shoestore.app.config

import io.spring.shoestore.core.users.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
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
    @Autowired
    private lateinit var customLogoutHandler: LogoutHandler
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeRequests()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // For static resources
            .requestMatchers("/products/").permitAll()
            .requestMatchers("/product/**").permitAll()
            .requestMatchers("/user/**").hasAuthority("US")
            .requestMatchers("/admin/**").hasAuthority("AD")
//            .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
            .and()
                .oauth2Login()
                .loginPage("/oauth2/authorization/auth0")
            .successHandler(customAuthenticationSuccessHandler())
            .failureHandler(customAuthenticationFailureHandler())
            .and()
            .logout()
            .logoutUrl("/logout")
            .addLogoutHandler(customLogoutHandler)
            .logoutSuccessUrl("/login")
            .clearAuthentication(true)
            .invalidateHttpSession(true)
            .and()
            .exceptionHandling()
            .accessDeniedPage("/error");
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