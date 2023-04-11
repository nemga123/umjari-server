package com.umjari.server.domain.groupqna.repository

import com.umjari.server.domain.groupqna.model.GroupQnaReply
import org.springframework.data.jpa.repository.JpaRepository

interface GroupQnaReplyRepository : JpaRepository<GroupQnaReply, Long?> {
    fun existsByQnaId(qnaId: Long): Boolean
}
