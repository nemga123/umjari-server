package com.umjari.server.domain.auth

import com.umjari.server.domain.auth.model.AuthenticationToken
import com.umjari.server.domain.auth.model.UserPrincipal
import com.umjari.server.domain.user.repository.UserRepository
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.lang.RuntimeException
import java.util.Date

@Component
class JwtTokenProvider(
    private val userRepository: UserRepository,
) {
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    val tokenPrefix = "Bearer "
    val header = "Authorization"

    @Value("\${app.jwt.jwt-secret-key}")
    private val jwtSecretKey: String? = null

    @Value("\${app.jwt.jwt-expiration-in-ms}")
    private val jwtExpirationInMs: Long? = null

    fun generateToken(userId: String): String {
        val claims: MutableMap<String, String> = hashMapOf("userId" to userId)
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs!!)

        val keyBytes = Decoders.BASE64.decode(jwtSecretKey)
        val key = Keys.hmacShaKeyFor(keyBytes)

        val token = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact()
        return tokenPrefix + token
    }

    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserPrincipal
        return generateToken(userPrincipal.user.userId)
    }

    fun getAuthenticationTokenFromJwt(token: String): Authentication {
        val tokenWithOutPrefix = removePrefix(token)

        val keyBytes = Decoders.BASE64.decode(jwtSecretKey)
        val key = Keys.hmacShaKeyFor(keyBytes)

        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(tokenWithOutPrefix)
            .body

        val userId = claims.get("userId", String::class.java)
        val currentUser = userRepository.findByUserId(userId)
            ?: throw RuntimeException("$userId does not exist.")
        val userPrincipal = UserPrincipal(currentUser)
        return AuthenticationToken(userPrincipal, null)
    }
    fun validateToken(authToken: String?): Boolean {
        if (authToken.isNullOrEmpty()) {
            logger.error("Token is not provided")
            return false
        }
        if (!authToken.startsWith(tokenPrefix)) {
            logger.error("Token not match type Bearer")
            return false
        }
        val authTokenWithoutPrefix = removePrefix(authToken)

        val keyBytes = Decoders.BASE64.decode(jwtSecretKey)
        val key = Keys.hmacShaKeyFor(keyBytes)

        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authTokenWithoutPrefix)

            return true
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
        }
        return false
    }

    fun removePrefix(tokenWithPrefix: String): String {
        return tokenWithPrefix.replace(tokenPrefix, "").trim { it <= ' ' }
    }
}
