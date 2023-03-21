package com.umjari.server.domain.group.controller

import com.umjari.server.domain.group.dto.GroupDto
import com.umjari.server.domain.group.service.GroupService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Group APIs", description = "그룹 API")
@RestController
@RequestMapping("/api/v1/group")
class GroupController(
    private val groupService: GroupService,
) {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun createGroup(
        @Valid @RequestBody
        createGroupRequest: GroupDto.CreateGroupRequest,
    ): GroupDto.GroupDetailResponse {
        return groupService.createGroup(createGroupRequest)
    }

    @GetMapping("/{group_id}/")
    @ResponseStatus(HttpStatus.OK)
    fun getGroup(@PathVariable("group_id") groupId: Long): GroupDto.GroupDetailResponse {
        return groupService.getGroup(groupId)
    }

    @PutMapping("/{group_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateGroup(
        @PathVariable("group_id") groupId: Long,
        @Valid @RequestBody
        updateGroupRequest: GroupDto.UpdateGroupRequest,
    ) {
        groupService.updateGroup(groupId, updateGroupRequest)
    }
}
