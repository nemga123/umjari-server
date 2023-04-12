package com.umjari.server.domain.groupqna.repository

import com.umjari.server.domain.groupqna.dto.GroupQnaDto
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
          qna.id AS id, qna.title AS title, qna.isPrivate AS isPrivate, qna.authorNickname AS nickname, author.id AS authorId, author.nickname AS authorNickname, COUNT (reply.id) AS replyCount
        FROM
          GroupQna AS qna
          JOIN User AS author ON qna.author.id = author.id
          LEFT JOIN GroupQnaReply AS reply ON reply.qna.id = qna.id
        WHERE
          qna.group.id = :groupId
        GROUP BY qna.id, author.id
        """,
        countQuery = """
            SELECT COUNT (qna) FROM GroupQna AS qna WHERE qna.group.id = :groupId
        """,
    )
    fun getSimpleResponseByGroupId(@Param("groupId") groupId: Long, pageable: Pageable): Page<GroupQnaDto.SimpleQnaDto>

    fun getByIdAndGroupId(id: Long, groupId: Long): GroupQna?
}
