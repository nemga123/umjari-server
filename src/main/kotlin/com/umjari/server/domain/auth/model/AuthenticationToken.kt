package com.umjari.server.domain.auth.model

import org.springframework.security.authentication.AbstractAuthenticationToken

class AuthenticationToken(
    private val principal: UserPrincipal,
    private var accessToken: String?,
) : AbstractAuthenticationToken(principal.authorities) {
    init {
        if (authorities == null) {
            super.setAuthenticated(false)
        } else {
            super.setAuthenticated(true)
        }
    }

    override fun getCredentials(): Any? {
        return accessToken
    }

    override fun getPrincipal(): Any {
        return principal
    }

    override fun eraseCredentials() {
        super.eraseCredentials()
        accessToken = null
    }
}
