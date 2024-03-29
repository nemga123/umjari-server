package com.umjari.server.domain.post.service

import com.umjari.server.domain.post.dto.BoardType
import com.umjari.server.domain.post.dto.CommunityPostDto
import com.umjari.server.domain.post.exception.PostIdNotFoundException
import com.umjari.server.domain.post.exception.PostPermissionNotAuthorizedException
import com.umjari.server.domain.post.model.CommunityPost
import com.umjari.server.domain.post.model.PostLike
import com.umjari.server.domain.post.repository.CommunityPostRepository
import com.umjari.server.domain.post.repository.PostLikeRepository
import com.umjari.server.domain.post.specification.PostSpecification
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommunityPostService(
    private val communityPostRepository: CommunityPostRepository,
    private val postLikeRepository: PostLikeRepository,
    private val postLikeService: PostLikeService,
    private val postReplyLikeService: PostReplyLikeService,
) {
    fun createCommunityPost(
        boardName: String,
        createCommunityPostRequest: CommunityPostDto.CreateCommunityPostRequest,
        user: User,
    ): CommunityPostDto.PostDetailResponse {
        val post = CommunityPost(
            author = user,
            board = BoardType.boardNameToBoardType(boardName),
            authorNickname = user.nickname,
            title = createCommunityPostRequest.title!!,
            content = createCommunityPostRequest.content!!,
            isAnonymous = createCommunityPostRequest.isAnonymous,
        ).also { post -> communityPostRepository.save(post) }

        return if (post.isAnonymous) {
            CommunityPostDto.AnonymousPostDetailResponse(post)
        } else {
            CommunityPostDto.NotAnonymousPostDetailResponse(post)
        }
    }

    fun updateCommunityPost(
        boardName: String,
        postId: Long,
        updateCommunityPostRequest: CommunityPostDto.UpdateCommunityPostRequest,
        user: User,
    ) {
        val post = communityPostRepository.findByBoardAndId(BoardType.boardNameToBoardType(boardName), postId)
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
        val post = communityPostRepository.findByBoardAndId(BoardType.boardNameToBoardType(boardName), postId)
            ?: throw PostIdNotFoundException(postId)

        if (post.author.id != user.id) throw PostPermissionNotAuthorizedException()

        communityPostRepository.delete(post)
    }

    fun getCommunityPost(boardName: String, postId: Long, user: User?): CommunityPostDto.PostDetailResponse {
        val post = communityPostRepository.findByBoardAndId(BoardType.boardNameToBoardType(boardName), postId)
            ?: throw PostIdNotFoundException(postId)
        val likeList = postLikeRepository.getAllByPostId(postId)
        val replyIdToLikeList = postReplyLikeService.getReplyIdToLikeListMap(post)
        return if (post.isAnonymous) {
            CommunityPostDto.AnonymousPostDetailResponse(post, user, likeList, replyIdToLikeList)
        } else {
            CommunityPostDto.NotAnonymousPostDetailResponse(post, user, likeList, replyIdToLikeList)
        }
    }

    fun getCommunityPostListByBoard(
        boardName: String,
        filterType: String,
        text: String?,
        pageable: Pageable,
        currentUser: User?,
    ): PageResponse<CommunityPostDto.PostSimpleResponse> {
        val postList = if (text.isNullOrBlank()) {
            if (boardName.uppercase() == "ALL") {
                communityPostRepository.findAllFetchReplies(pageable)
            } else {
                communityPostRepository.findByBoard(BoardType.boardNameToBoardType(boardName), pageable)
            }
        } else {
            val filteredBy = PostSpecification.FilterType.paramToFilterType(filterType)
            if (boardName.uppercase() == "ALL") {
                val postSpec = PostSpecification().build(filteredBy, text)
                communityPostRepository.findAll(postSpec, pageable)
            } else {
                val postSpec = PostSpecification(BoardType.boardNameToBoardType(boardName)).build(filteredBy, text)
                communityPostRepository.findAll(postSpec, pageable)
            }
        }
        val postIdToLikeList = postLikeService.getPostIdToLikeListMap(postList)
        val postResponses = buildPostPageResponse(postList, postIdToLikeList, currentUser)
        return PageResponse(postResponses, pageable.pageNumber)
    }

    fun buildPostPageResponse(
        postList: Page<CommunityPost>,
        postIdToReplyList: Map<Long, List<PostLike>>,
        currentUser: User?,
    ): Page<CommunityPostDto.PostSimpleResponse> {
        return postList.map {
            if (it.isAnonymous) {
                CommunityPostDto.AnonymousPostSimpleResponse(it, currentUser, postIdToReplyList[it.id] ?: emptyList())
            } else {
                CommunityPostDto.NotAnonymousPostSimpleResponse(
                    it,
                    currentUser,
                    postIdToReplyList[it.id] ?: emptyList(),
                )
            }
        }
    }
}
