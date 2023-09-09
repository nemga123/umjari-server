package com.umjari.server.domain.post.service

import com.umjari.server.domain.post.dto.BoardType
import com.umjari.server.domain.post.exception.ReplyIdNotFoundException
import com.umjari.server.domain.post.model.CommunityPost
import com.umjari.server.domain.post.model.PostReplyLike
import com.umjari.server.domain.post.repository.CommunityPostReplyRepository
import com.umjari.server.domain.post.repository.PostReplyLikeRepository
import com.umjari.server.domain.user.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostReplyLikeService(
    private val communityPostReplyRepository: CommunityPostReplyRepository,
    private val postReplyLikeRepository: PostReplyLikeRepository,
) {
    @Transactional
    fun updateLikeStatus(boardName: String, postId: Long, replyId: Long, user: User) {
        val reply = communityPostReplyRepository.getByPost_BoardAndPostIdAndId(
            BoardType.boardNameToBoardType(boardName),
            postId,
            replyId,
        )
            ?: throw ReplyIdNotFoundException(replyId)
        if (postReplyLikeRepository.existsByUserIdAndReplyId(user.id, replyId)) {
            postReplyLikeRepository.deleteByUserIdAndReplyId(user.id, replyId)
        } else {
            PostReplyLike(user = user, reply = reply)
                .also { postReplyLike -> postReplyLikeRepository.save(postReplyLike) }
        }
    }

    fun getReplyIdToLikeListMap(post: CommunityPost): Map<Long, List<PostReplyLike>> {
        val replyIds = post.replies.map { it.id }
        val replyLikeList = postReplyLikeRepository.getAllByReplyIdIn(replyIds)
        return replyLikeList.groupBy { it.reply.id }
    }
}
