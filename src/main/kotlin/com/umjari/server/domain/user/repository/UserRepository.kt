package com.umjari.server.domain.user.repository

import com.umjari.server.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, Long?> {
    fun findByUserId(userId: String): User?

    @Query(
        """
        SELECT u FROM User AS u WHERE u.userId IN :userIds
    """,
    )
    fun findUserIdsByUserIdIn(@Param("userIds") userIds: Set<String>): Set<User>

    fun existsByUserId(userId: String): Boolean
    fun existsByNickname(nickname: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun existsByProfileName(profileName: String): Boolean

    @Query(
        """
            SELECT user FROM User AS user
                LEFT JOIN FETCH user.career AS gm LEFT JOIN FETCH gm.group WHERE user.profileName = :profileName
        """,
    )
    fun findByProfileNameWithCareer(@Param("profileName") profileName: String): User?
    fun findByProfileName(profileName: String): User?

    fun existsByNicknameAndIdNot(nickname: String, id: Long): Boolean
    fun existsByProfileNameAndIdNot(profileName: String, id: Long): Boolean

    fun existsByIdAndRolesLike(id: Long, roles: String): Boolean

    fun findByUserIdAndEmail(userId: String, email: String): User?

    fun findByEmail(email: String): User?
}
