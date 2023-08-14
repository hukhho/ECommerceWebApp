//package io.spring.shoestore.app.config
//
//import io.spring.shoestore.core.security.StoreAuthProvider
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.security.core.userdetails.UserDetailsService
//
//@Configuration
//class AuthConfig {
//
//    @Bean
//    fun getStoreAuthProvider(userDetailsService: UserDetailsService): StoreAuthProvider {
//        return StoreAuthProviderImpl(userDetailsService)
//    }
//
//    @Bean
//    fun authenticationProvider(userDetailsService: UserDetailsService, passwordEncoder: PasswordEncoder): AuthenticationProvider {
//        val authProvider = DaoAuthenticationProvider()
//        authProvider.setUserDetailsService(userDetailsService)
//        authProvider.setPasswordEncoder(passwordEncoder)
//        return authProvider
//    }
//}