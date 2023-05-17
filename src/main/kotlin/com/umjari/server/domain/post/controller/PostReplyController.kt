package com.umjari.server.domain.post.controller

import com.umjari.server.domain.post.dto.PostReplyDto
import com.umjari.server.domain.post.service.CommunityPostReplyService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "community post reply", description = "커뮤니티 댓글 APIs")
@RestController
@RequestMapping("/api/v1/board/{inst_name}/post/{post_id}/reply")
class PostReplyController(
    private val communityPostReplyService: CommunityPostReplyService,
) {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPostReply(
        @PathVariable("inst_name") boardName: String,
        @PathVariable("post_id") postId: Long,
        @Valid @RequestBody
        createReplyRequest: PostReplyDto.CreateReplyRequest,
        @CurrentUser user: User,
    ) {
        communityPostReplyService.createReplyOnPost(boardName, postId, createReplyRequest, user)
    }

    @PutMapping("/{reply_id}/")
    @ResponseStatus(HttpStatus.CREATED)
    fun updatePostReply(
        @PathVariable("inst_name") boardName: String,
        @PathVariable("post_id") postId: Long,
        @PathVariable("reply_id") replyId: Long,
        @Valid @RequestBody
        createReplyRequest: PostReplyDto.CreateReplyRequest,
        @CurrentUser user: User,
    ) {
        communityPostReplyService.updateReplyOnPost(boardName, postId, replyId, createReplyRequest, user)
    }
}
