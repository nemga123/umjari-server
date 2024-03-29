package com.umjari.server.domain.mypage.service

import com.umjari.server.domain.groupqna.repository.GroupQnaRepository
import com.umjari.server.domain.mypage.dto.MyPageDto
import com.umjari.server.domain.post.dto.CommunityPostDto
import com.umjari.server.domain.post.repository.CommunityPostReplyRepository
import com.umjari.server.domain.post.repository.CommunityPostRepository
import com.umjari.server.domain.post.service.CommunityPostService
import com.umjari.server.domain.post.service.PostLikeService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class MyPageService(
    private val communityPostRepository: CommunityPostRepository,
    private val communityPostService: CommunityPostService,
    private val communityPostReplyRepository: CommunityPostReplyRepository,
    private val postLikeService: PostLikeService,
    private val groupQnaRepository: GroupQnaRepository,
) {
    fun getMyPostList(pageable: Pageable, currentUser: User): PageResponse<MyPageDto.MyPostListResponse> {
        val postList = communityPostRepository.findByAuthorId(currentUser.id, pageable)
        val postIdToLikeList = postLikeService.getPostIdToLikeListMap(postList)
        val postResponses = postList.map {
            MyPageDto.MyPostListResponse(
                it,
                postIdToLikeList.getOrDefault(it.id, emptyList()),
            )
        }
        return PageResponse(postResponses, pageable.pageNumber)
    }

    fun getLikedPostList(pageable: Pageable, currentUser: User): PageResponse<CommunityPostDto.PostSimpleResponse> {
        val postList = communityPostRepository.findAllLikedPosts(currentUser.id, pageable)
        val postIdToLikeList = postLikeService.getPostIdToLikeListMap(postList)
        val postResponses = communityPostService.buildPostPageResponse(postList, postIdToLikeList, currentUser)
        return PageResponse(postResponses, pageable.pageNumber)
    }

    fun getMyPostReplyList(pageable: Pageable, currentUser: User): PageResponse<MyPageDto.MyPostReplyListResponse> {
        val replyResponses = communityPostReplyRepository.getAllMyReplies(currentUser.id, pageable)
            .map { MyPageDto.MyPostReplyListResponse(it) }
        return PageResponse(replyResponses, pageable.pageNumber)
    }

    fun getRepliedPostList(pageable: Pageable, currentUser: User): PageResponse<CommunityPostDto.PostSimpleResponse> {
        val postList = communityPostRepository.findAllRepliedPosts(currentUser.id, pageable)
        val postIdToLikeList = postLikeService.getPostIdToLikeListMap(postList)
        val postResponses = communityPostService.buildPostPageResponse(postList, postIdToLikeList, currentUser)
        return PageResponse(postResponses, pageable.pageNumber)
    }

    fun getMyQnaList(pageable: Pageable, currentUser: User): PageResponse<MyPageDto.MyQnaListResponse> {
        val qnaResponse = groupQnaRepository.findAllMyQnaList(currentUser.id, pageable)
            .map { MyPageDto.MyQnaListResponse(it) }
        return PageResponse(qnaResponse, pageable.pageNumber)
    }
}
