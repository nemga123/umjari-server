package com.umjari.server.domain.mailverification.repository

import com.umjari.server.domain.mailverification.model.VerifyToken
import org.springframework.data.jpa.repository.JpaRepository

interface VerifyTokenRepository : JpaRepository<VerifyToken, Long?> {
    fun findByTokenAndEmail(token: String, email: String): VerifyToken?

    fun findTopByEmailOrderByCreatedAtDesc(email: String): VerifyToken?

    fun deleteAllByEmail(email: String)
}
