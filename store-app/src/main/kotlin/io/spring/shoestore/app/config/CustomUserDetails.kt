package io.spring.shoestore.app.config

import io.spring.shoestore.core.users.UserId
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val username: String,
    private val password: String,
    private val email: String,
    private var fullName: String,
    private val userID: UserId,
    private val authorities: Collection<GrantedAuthority>
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    fun getEmail(): String {
        return email
    }
    fun getFullName(): String {
        return fullName
    }
    fun setFullName(fullName: String) {
        this.fullName = fullName
    }
    fun getUserID(): UserId {
        return userID
    }
}
