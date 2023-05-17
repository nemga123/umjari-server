package com.umjari.server.domain.post.service

import com.umjari.server.domain.group.model.Instrument
import com.umjari.server.domain.post.dto.CommunityPostDto
import com.umjari.server.domain.post.exception.CommunityPostIdNotFoundException
import com.umjari.server.domain.post.exception.CommunityPostPermissionNotAuthorizedException
import com.umjari.server.domain.post.model.CommunityPost
import com.umjari.server.domain.post.repository.CommunityPostRepository
import com.umjari.server.domain.user.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommunityPostService(
    private val communityPostRepository: CommunityPostRepository,
) {
    fun createCommunityPost(
        boardName: String,
        createCommunityPostRequest: CommunityPostDto.CreateCommunityPostRequest,
        user: User,
    ): CommunityPostDto.PostDetailResponse {
        val post = CommunityPost(
            author = user,
            board = Instrument.valueOf(boardName.uppercase()),
            authorNickname = user.nickname,
            title = createCommunityPostRequest.title!!,
            content = createCommunityPostRequest.content!!,
            isAnonymous = createCommunityPostRequest.isAnonymous,
        )

        val postObject = communityPostRepository.save(post)

        return if (postObject.isAnonymous) {
            CommunityPostDto.AnonymousPostDetailResponse(postObject, user)
        } else {
            CommunityPostDto.NotAnonymousPostDetailResponse(postObject, user)
        }
    }

    fun updateCommunityPost(
        boardName: String,
        postId: Long,
        updateCommunityPostRequest: CommunityPostDto.UpdateCommunityPostRequest,
        user: User,
    ) {
        val post = communityPostRepository.findByBoardAndId(Instrument.valueOf(boardName.uppercase()), postId)
            ?: throw CommunityPostIdNotFoundException(postId)

        if (post.author.id != user.id) throw CommunityPostPermissionNotAuthorizedException()

        with(post) {
            board = updateCommunityPostRequest.board
            title = updateCommunityPostRequest.title!!
            content = updateCommunityPostRequest.content!!
            isAnonymous = updateCommunityPostRequest.isAnonymous
        }

        communityPostRepository.save(post)
    }

    @Transactional
    fun deleteCommunityPost(boardName: String, postId: Long, user: User) {
        val post = communityPostRepository.findByBoardAndId(Instrument.valueOf(boardName.uppercase()), postId)
            ?: throw CommunityPostIdNotFoundException(postId)

        if (post.author.id != user.id) throw CommunityPostPermissionNotAuthorizedException()

        communityPostRepository.delete(post)
    }

    fun getCommunityPost(boardName: String, postId: Long, user: User): CommunityPostDto.PostDetailResponse {
        val post = communityPostRepository.findByBoardAndId(Instrument.valueOf(boardName.uppercase()), postId)
            ?: throw CommunityPostIdNotFoundException(postId)

        return if (post.isAnonymous) {
            CommunityPostDto.AnonymousPostDetailResponse(post, user)
        } else {
            CommunityPostDto.NotAnonymousPostDetailResponse(post, user)
        }
    }
}
