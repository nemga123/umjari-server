package com.umjari.server.domain.post.service

import com.umjari.server.domain.group.model.Instrument
import com.umjari.server.domain.post.dto.CommunityPostDto
import com.umjari.server.domain.post.exception.BoardNameNotFoundException
import com.umjari.server.domain.post.exception.PostIdNotFoundException
import com.umjari.server.domain.post.exception.PostPermissionNotAuthorizedException
import com.umjari.server.domain.post.model.CommunityPost
import com.umjari.server.domain.post.repository.CommunityPostRepository
import com.umjari.server.domain.user.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException

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
            board = boardNameToInstrumentEnum(boardName),
            authorNickname = user.nickname,
            title = createCommunityPostRequest.title!!,
            content = createCommunityPostRequest.content!!,
            isAnonymous = createCommunityPostRequest.isAnonymous,
        )

        val postObject = communityPostRepository.save(post)

        return if (postObject.isAnonymous) {
            CommunityPostDto.AnonymousPostDetailResponse(postObject, user, emptyList())
        } else {
            CommunityPostDto.NotAnonymousPostDetailResponse(postObject, user, emptyList())
        }
    }

    fun updateCommunityPost(
        boardName: String,
        postId: Long,
        updateCommunityPostRequest: CommunityPostDto.UpdateCommunityPostRequest,
        user: User,
    ) {
        val post = communityPostRepository.findByBoardAndId(boardNameToInstrumentEnum(boardName), postId)
            ?: throw PostIdNotFoundException(postId)

        if (post.author.id != user.id) throw PostPermissionNotAuthorizedException()

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
        val post = communityPostRepository.findByBoardAndId(boardNameToInstrumentEnum(boardName), postId)
            ?: throw PostIdNotFoundException(postId)

        if (post.author.id != user.id) throw PostPermissionNotAuthorizedException()

        communityPostRepository.delete(post)
    }

    fun getCommunityPost(boardName: String, postId: Long, user: User): CommunityPostDto.PostDetailResponse {
        val post = communityPostRepository.findByBoardAndId(boardNameToInstrumentEnum(boardName), postId)
            ?: throw PostIdNotFoundException(postId)

        return if (post.isAnonymous) {
            CommunityPostDto.AnonymousPostDetailResponse(post, user)
        } else {
            CommunityPostDto.NotAnonymousPostDetailResponse(post, user)
        }
    }

    private fun boardNameToInstrumentEnum(boardName: String): Instrument {
        try {
            return Instrument.valueOf(boardName.uppercase())
        } catch (e: IllegalArgumentException) {
            throw BoardNameNotFoundException(boardName.uppercase())
        }
    }
}