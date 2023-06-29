package com.umjari.server.domain.mypage.service

import com.umjari.server.domain.mypage.dto.MyPageDto
import com.umjari.server.domain.post.dto.CommunityPostDto
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
    private val postLikeService: PostLikeService,
) {
    fun getMyPostList(pageable: Pageable, currentUser: User): PageResponse<MyPageDto.MyPostListResponse> {
        val postList = communityPostRepository.findByAuthorId(currentUser.id, pageable)
        val postIds = postList.map { it.id }.toList()
        val postIdToLikeList = postLikeService.getPostIdToLikeListMap(postIds)
        val postResponses = postList.map { MyPageDto.MyPostListResponse(it, postIdToLikeList[it.id]!!) }
        return PageResponse(postResponses, pageable.pageNumber)
    }

    fun getLikedPostList(pageable: Pageable, currentUser: User): PageResponse<CommunityPostDto.PostSimpleResponse> {
        val postList = communityPostRepository.findAllLikedPosts(currentUser.id, pageable)
        val postIds = postList.map { it.id }.toList()
        val postIdToLikeList = postLikeService.getPostIdToLikeListMap(postIds)
        val postResponses = communityPostService.buildPostPageResponse(postList, postIdToLikeList, currentUser)
        return PageResponse(postResponses, pageable.pageNumber)
    }
}
