package com.umjari.server.domain.post.service

import com.umjari.server.domain.group.model.Instrument
import com.umjari.server.domain.post.dto.PostReplyDto
import com.umjari.server.domain.post.exception.PostIdNotFoundException
import com.umjari.server.domain.post.exception.ReplyIdNotFoundException
import com.umjari.server.domain.post.exception.ReplyPermissionNotAuthorizedException
import com.umjari.server.domain.post.model.CommunityPostReply
import com.umjari.server.domain.post.repository.CommunityPostReplyRepository
import com.umjari.server.domain.post.repository.CommunityPostRepository
import com.umjari.server.domain.user.model.User
import org.springframework.stereotype.Service

@Service
class CommunityPostReplyService(
    private val communityPostReplyRepository: CommunityPostReplyRepository,
    private val communityPostRepository: CommunityPostRepository,
) {
    fun createReplyOnPost(
        boardName: String,
        postId: Long,
        createReplyRequest: PostReplyDto.CreateReplyRequest,
        user: User,
    ) {
        val post = communityPostRepository.findByBoardAndId(Instrument.valueOf(boardName.uppercase()), postId)
            ?: throw PostIdNotFoundException(postId)

        val reply = CommunityPostReply(
            author = user,
            authorNickname = user.nickname,
            content = createReplyRequest.content!!,
            isAnonymous = createReplyRequest.isAnonymous,
            post = post,
        )

        communityPostReplyRepository.save(reply)
    }

    fun updateReplyOnPost(
        boardName: String,
        postId: Long,
        replyId: Long,
        createReplyRequest: PostReplyDto.CreateReplyRequest,
        user: User,
    ) {
        val reply = communityPostReplyRepository.getByPost_BoardAndPostIdAndId(
            Instrument.valueOf(boardName.uppercase()),
            postId,
            replyId,
        )
            ?: throw ReplyIdNotFoundException(replyId)

        if (reply.author.id != user.id) throw ReplyPermissionNotAuthorizedException()

        with(reply) {
            content = createReplyRequest.content!!
            isAnonymous = createReplyRequest.isAnonymous
        }

        communityPostReplyRepository.save(reply)
    }

    fun deleteReplyOnPost(boardName: String, postId: Long, replyId: Long, user: User) {
        val reply = communityPostReplyRepository.getByPost_BoardAndPostIdAndId(
            Instrument.valueOf(boardName.uppercase()),
            postId,
            replyId,
        )
            ?: throw ReplyIdNotFoundException(replyId)

        if (reply.author.id != user.id) throw ReplyPermissionNotAuthorizedException()

        reply.isDeleted = true
        communityPostReplyRepository.save(reply)
    }
}
