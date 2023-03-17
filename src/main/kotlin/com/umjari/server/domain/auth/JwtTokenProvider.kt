package com.umjari.server.domain.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProvider(
) {
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    val tokenPrefix = "Bearer "
    val header = "Authorization"

    @Value("\${app.jwt.jwt-secret-key}")
    private val jwtSecretKey: String? = null

    @Value("\${app.jwt.jwt-expiration-in-ms}")
    private val jwtExpirationInMs: Long? = null

    private val keyBytes = Decoders.BASE64.decode(jwtSecretKey)
    private val key = Keys.hmacShaKeyFor(keyBytes)

    fun generateToken(userId: String): String {
        val claims: MutableMap<String, String> = hashMapOf("userId" to userId)
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs!!)
        val token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact()
        return tokenPrefix + token
    }

    fun generateToken(authentication: Authentication): String {
        val verificationTokenPrincipal = authentication.principal as VerificationTokenPrincipal
        return generateToken(verificationTokenPrincipal.verificationToken.userId)
    }
}