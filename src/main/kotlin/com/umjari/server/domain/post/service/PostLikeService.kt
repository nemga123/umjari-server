package com.umjari.server.domain.post.service

import com.umjari.server.domain.post.dto.BoardType
import com.umjari.server.domain.post.exception.PostIdNotFoundException
import com.umjari.server.domain.post.model.PostLike
import com.umjari.server.domain.post.repository.CommunityPostRepository
import com.umjari.server.domain.post.repository.PostLikeRepository
import com.umjari.server.domain.user.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostLikeService(
    private val communityPostRepository: CommunityPostRepository,
    private val postLikeRepository: PostLikeRepository,
) {
    @Transactional
    fun updateLikeStatus(boardName: String, postId: Long, user: User) {
        val post = communityPostRepository.findByBoardAndId(BoardType.boardNameToBoardType(boardName), postId)
            ?: throw PostIdNotFoundException(postId)
        if (postLikeRepository.existsByUserIdAndPostId(user.id, postId)) {
            postLikeRepository.deleteByUserIdAndPostId(user.id, postId)
        } else {
            val obj = PostLike(user = user, post = post)
            postLikeRepository.save(obj)
        }
    }

    fun getPostIdToLikeListMap(postIds: List<Long>): Map<Long, List<PostLike>> {
        val replyList = postLikeRepository.getAllByPostIdIn(postIds)
        return replyList.groupBy { it.post.id }
    }
}
