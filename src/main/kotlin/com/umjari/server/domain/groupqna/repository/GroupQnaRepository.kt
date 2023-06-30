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
            qna.id AS id,
            qna.title AS title,
            qna.isAnonymous AS anonymous,
            qna.authorNickname AS nickname,
            author.id AS authorId,
            author.profileName AS authorProfileName,
            author.profileImage AS authorProfileImage,
            COUNT (reply.id) AS replyCount,
            qna.createdAt AS createAt,
            qna.updatedAt AS updatedAt
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
    fun getSimpleResponseByGroupIdWithReplyCounts(
        @Param("groupId") groupId: Long,
        pageable: Pageable,
    ): Page<GroupQnaDto.SimpleQnaDto>

    @Query(
        value = """
        SELECT
            qna.id AS id,
            qna.title AS title,
            qna.isAnonymous AS anonymous,
            qna.authorNickname AS nickname,
            author.id AS authorId,
            author.profileName AS authorProfileName,
            author.profileImage AS authorProfileImage,
            COUNT (reply.id) AS replyCount,
            qna.createdAt AS createAt,
            qna.updatedAt AS updatedAt
        FROM
          GroupQna AS qna
          JOIN User AS author ON qna.author.id = author.id
          LEFT JOIN GroupQnaReply AS reply ON reply.qna.id = qna.id
        WHERE
          qna.group.id = :groupId
          AND (qna.title LIKE %:searchText% OR qna.content LIKE %:searchText%)
        GROUP BY qna.id, author.id
        """,
        countQuery = """
            SELECT COUNT (qna) FROM GroupQna AS qna
                WHERE qna.group.id = :groupId
                    AND (qna.title LIKE %:searchText% OR qna.content LIKE %:searchText%)
        """,
    )
    fun getSimpleResponseByGroupIdAndSearchTextWithReplyCounts(
        @Param("groupId") groupId: Long,
        @Param("searchText") searchText: String,
        pageable: Pageable,
    ): Page<GroupQnaDto.SimpleQnaDto>

    fun getByIdAndGroupId(id: Long, groupId: Long): GroupQna?

    @Query(
        """
            SELECT qna
                FROM GroupQna AS qna
                LEFT JOIN FETCH qna.replies
            WHERE qna.author.id = :authorId
        """,
        countQuery = """
            SELECT COUNT (*) FROM GroupQna AS qna WHERE qna.author.id = :authorId
        """,
    )
    fun findAllMyQnaList(@Param("authorId") authorId: Long, pageable: Pageable): Page<GroupQna>
}
