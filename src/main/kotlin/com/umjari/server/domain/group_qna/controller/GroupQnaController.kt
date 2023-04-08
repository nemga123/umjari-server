package com.umjari.server.domain.group_qna.controller

import com.umjari.server.domain.group_qna.dto.GroupQnaDto
import com.umjari.server.domain.group_qna.service.GroupQnaService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import com.umjari.server.global.pagination.PageResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "QnA APIs", description = "QnA API")
@RestController
@RequestMapping("/api/v1/group/{group_id}/qna")
class GroupQnaController(
    private val groupQnaService: GroupQnaService,
) {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun createQna(
        @PathVariable("group_id") groupId: Long,
        @Valid @RequestBody
        createQnaRequest: GroupQnaDto.CreateQnaRequest,
        @CurrentUser user: User,
    ): GroupQnaDto.NotPrivateQnaResponse {
        return groupQnaService.createQna(createQnaRequest, user, groupId)
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    fun getQnaList(
        @PathVariable("group_id") groupId: Long,
        @PageableDefault(
            size = 20,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC,
        ) pageable: Pageable,
        @CurrentUser user: User?,
    ): PageResponse<GroupQnaDto.QnaResponse> {
        return groupQnaService.getQnaListByGroupId(groupId, user, pageable)
    }
}
