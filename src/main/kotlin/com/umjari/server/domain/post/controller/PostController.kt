package com.umjari.server.domain.post.controller

import com.umjari.server.domain.post.dto.CommunityPostDto
import com.umjari.server.domain.post.service.CommunityPostService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import com.umjari.server.global.pagination.PageResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "community post", description = "커뮤니티 게시글 APIs")
@RestController
@RequestMapping("/api/v1/board/{board_type}/post")
class PostController(
    private val communityPostService: CommunityPostService,
) {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun createCommunityPost(
        @PathVariable("board_type") boardName: String,
        @Valid @RequestBody
        createCommunityPostRequest: CommunityPostDto.CreateCommunityPostRequest,
        @CurrentUser user: User,
    ): CommunityPostDto.PostDetailResponse {
        return communityPostService.createCommunityPost(boardName, createCommunityPostRequest, user)
    }

    @PutMapping("/{post_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateCommunityPost(
        @PathVariable("board_type") boardName: String,
        @PathVariable("post_id") postId: Long,
        @Valid @RequestBody
        updateCommunityPostRequest: CommunityPostDto.UpdateCommunityPostRequest,
        @CurrentUser user: User,
    ) {
        communityPostService.updateCommunityPost(boardName, postId, updateCommunityPostRequest, user)
    }

    @DeleteMapping("/{post_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCommunityPost(
        @PathVariable("board_type") boardName: String,
        @PathVariable("post_id") postId: Long,
        @CurrentUser user: User,
    ) {
        communityPostService.deleteCommunityPost(boardName, postId, user)
    }

    @GetMapping("/{post_id}/")
    @ResponseStatus(HttpStatus.OK)
    fun getCommunityPost(
        @PathVariable("board_type") boardName: String,
        @PathVariable("post_id") postId: Long,
        @CurrentUser user: User?,
    ): CommunityPostDto.PostDetailResponse {
        return communityPostService.getCommunityPost(boardName, postId, user)
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    fun getCommunityPost(
        @PathVariable("board_type") boardName: String,
        @PageableDefault(
            size = 20,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC,
        ) pageable: Pageable,
        @CurrentUser user: User?,
    ): PageResponse<CommunityPostDto.PostSimpleResponse> {
        return communityPostService.getCommunityPostListByBoard(boardName, pageable, user)
    }
}
