package com.umjari.server.domain.auth.repository

import com.umjari.server.domain.auth.model.VerifyToken
import org.springframework.data.jpa.repository.JpaRepository

interface VerifyTokenRepository : JpaRepository<VerifyToken, Long?>
