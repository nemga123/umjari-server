package com.umjari.server.domain.user.repository

import com.umjari.server.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long?> {
    fun findByUserId(userId: String): User?
}
