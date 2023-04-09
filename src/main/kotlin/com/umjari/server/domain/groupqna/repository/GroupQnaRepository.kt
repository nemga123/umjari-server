package com.umjari.server.domain.groupqna.repository

import com.umjari.server.domain.groupqna.model.GroupQna
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GroupQnaRepository : JpaRepository<GroupQna, Long?> {
    @Query(
        value = """
        SELECT
          qna
        FROM
          GroupQna AS qna
          JOIN FETCH qna.author
        WHERE
          qna.group.id = :groupId
        """,
        countQuery = """
            SELECT COUNT (qna) FROM GroupQna AS qna WHERE qna.group.id = :groupId
        """,
    )
    fun getAllByGroupId(@Param("groupId") groupId: Long, pageable: Pageable): Page<GroupQna>

    fun getByIdAndGroupId(id: Long, groupId: Long): GroupQna?
}
