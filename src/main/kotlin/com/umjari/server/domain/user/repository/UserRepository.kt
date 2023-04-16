package com.umjari.server.domain.user.repository

import com.umjari.server.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, Long?> {
    fun findByUserId(userId: String): User?

    @Query(
        """
        SELECT u.userId FROM User AS u WHERE u.userId IN :userIds
    """,
    )
    fun findUserIdsByUserIdIn(@Param("userIds") userIds: List<String>): Set<String>

    fun existsByUserId(userId: String): Boolean
    fun existsByNickname(nickname: String): Boolean
    fun existsByEmail(email: String): Boolean
}
