package com.umjari.server.domain.auth.model

import com.umjari.server.domain.user.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(val user: User) : UserDetails {
    override fun getAuthorities(): List<GrantedAuthority> {
        val roles: List<String> = user.roles.split(",").filter { it.isNotEmpty() }
        return roles.map { role: String? -> SimpleGrantedAuthority(role) }
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.userId
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
}
