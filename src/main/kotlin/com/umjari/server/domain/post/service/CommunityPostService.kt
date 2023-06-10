package com.umjari.server.domain.post.service

import com.umjari.server.domain.post.dto.BoardType
import com.umjari.server.domain.post.dto.CommunityPostDto
import com.umjari.server.domain.post.exception.BoardNameNotFoundException
import com.umjari.server.domain.post.exception.PostIdNotFoundException
import com.umjari.server.domain.post.exception.PostPermissionNotAuthorizedException
import com.umjari.server.domain.post.model.CommunityPost
import com.umjari.server.domain.post.repository.CommunityPostRepository
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
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
            board = boardNameToBoardType(boardName),
            authorNickname = user.nickname,
            title = createCommunityPostRequest.title!!,
            content = createCommunityPostRequest.content!!,
            isAnonymous = createCommunityPostRequest.isAnonymous,
        )

        val postObject = communityPostRepository.save(post)

        return if (postObject.isAnonymous) {
            CommunityPostDto.AnonymousPostDetailResponse(postObject, emptyList())
        } else {
            CommunityPostDto.NotAnonymousPostDetailResponse(postObject, emptyList())
        }
    }

    fun updateCommunityPost(
        boardName: String,
        postId: Long,
        updateCommunityPostRequest: CommunityPostDto.UpdateCommunityPostRequest,
        user: User,
    ) {
        val post = communityPostRepository.findByBoardAndId(boardNameToBoardType(boardName), postId)
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
        val post = communityPostRepository.findByBoardAndId(boardNameToBoardType(boardName), postId)
            ?: throw PostIdNotFoundException(postId)

        if (post.author.id != user.id) throw PostPermissionNotAuthorizedException()

        communityPostRepository.delete(post)
    }

    fun getCommunityPost(boardName: String, postId: Long, user: User?): CommunityPostDto.PostDetailResponse {
        val post = communityPostRepository.findByBoardAndId(boardNameToBoardType(boardName), postId)
            ?: throw PostIdNotFoundException(postId)

        return if (post.isAnonymous) {
            CommunityPostDto.AnonymousPostDetailResponse(post, user)
        } else {
            CommunityPostDto.NotAnonymousPostDetailResponse(post, user)
        }
    }

    fun getCommunityPostListByBoard(
        boardName: String,
        pageable: Pageable,
        currentUser: User?,
    ): PageResponse<CommunityPostDto.PostSimpleResponse> {
        if (boardName.uppercase() == "ALL") {
            return getCommunityAllPostList(pageable, currentUser)
        } else {
            return getCommunityBoardPostList(boardName, pageable, currentUser)
        }
    }

    private fun getCommunityBoardPostList(
        boardName: String,
        pageable: Pageable,
        currentUser: User?,
    ): PageResponse<CommunityPostDto.PostSimpleResponse> {
        val postList = communityPostRepository.findByBoard(boardNameToBoardType(boardName), pageable)
        val postResponses = postList.map {
            if (it.isAnonymous) {
                CommunityPostDto.AnonymousPostSimpleResponse(it, currentUser)
            } else {
                CommunityPostDto.NotAnonymousPostSimpleResponse(it, currentUser)
            }
        }
        return PageResponse(postResponses, pageable.pageNumber)
    }

    private fun getCommunityAllPostList(
        pageable: Pageable,
        currentUser: User?,
    ): PageResponse<CommunityPostDto.PostSimpleResponse> {
        val postList = communityPostRepository.findAll(pageable)
        val postResponses = postList.map {
            if (it.isAnonymous) {
                CommunityPostDto.AnonymousPostSimpleResponse(it, currentUser)
            } else {
                CommunityPostDto.NotAnonymousPostSimpleResponse(it, currentUser)
            }
        }
        return PageResponse(postResponses, pageable.pageNumber)
    }

    private fun boardNameToBoardType(boardName: String): BoardType {
        try {
            return BoardType.valueOf(boardName.uppercase())
        } catch (e: IllegalArgumentException) {
            throw BoardNameNotFoundException(boardName.uppercase())
        }
    }
}
