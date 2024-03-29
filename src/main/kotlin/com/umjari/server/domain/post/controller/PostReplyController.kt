package com.umjari.server.domain.post.controller

import com.umjari.server.domain.post.dto.PostReplyDto
import com.umjari.server.domain.post.service.CommunityPostReplyService
import com.umjari.server.domain.post.service.PostReplyLikeService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "community post reply", description = "커뮤니티 댓글 APIs")
@RestController
@RequestMapping("/api/v1/board/{board_type}/post/{post_id}/reply")
class PostReplyController(
    private val communityPostReplyService: CommunityPostReplyService,
    private val postReplyLikeService: PostReplyLikeService,
) {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPostReply(
        @PathVariable("board_type") boardName: String,
        @PathVariable("post_id") postId: Long,
        @Valid @RequestBody
        createReplyRequest: PostReplyDto.CreateReplyRequest,
        @CurrentUser user: User,
    ) {
        communityPostReplyService.createReplyOnPost(boardName, postId, createReplyRequest, user)
    }

    @PutMapping("/{reply_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updatePostReply(
        @PathVariable("board_type") boardName: String,
        @PathVariable("post_id") postId: Long,
        @PathVariable("reply_id") replyId: Long,
        @Valid @RequestBody
        createReplyRequest: PostReplyDto.CreateReplyRequest,
        @CurrentUser user: User,
    ) {
        communityPostReplyService.updateReplyOnPost(boardName, postId, replyId, createReplyRequest, user)
    }

    @DeleteMapping("/{reply_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePostReply(
        @PathVariable("board_type") boardName: String,
        @PathVariable("post_id") postId: Long,
        @PathVariable("reply_id") replyId: Long,
        @CurrentUser user: User,
    ) {
        communityPostReplyService.deleteReplyOnPost(boardName, postId, replyId, user)
    }

    @PutMapping("/{reply_id}/likes/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateReplyLikeStatus(
        @PathVariable("board_type") boardName: String,
        @PathVariable("post_id") postId: Long,
        @PathVariable("reply_id") replyId: Long,
        @CurrentUser user: User,
    ) {
        postReplyLikeService.updateLikeStatus(boardName, postId, replyId, user)
    }
}
