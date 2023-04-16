package com.umjari.server.domain.groupqna.repository

import com.umjari.server.domain.groupqna.model.GroupQnaReply
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GroupQnaReplyRepository : JpaRepository<GroupQnaReply, Long?> {
    fun existsByQnaId(qnaId: Long): Boolean

    @Query(
        """
        SELECT reply FROM GroupQnaReply AS reply JOIN FETCH reply.author WHERE reply.qna.id = :qnaId
    """,
    )
    fun getAllByQnaIdWithUser(@Param("qnaId") qnaId: Long): List<GroupQnaReply>
}
