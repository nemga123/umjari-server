package com.umjari.server.domain.group.controller

import com.umjari.server.domain.group.dto.GroupDto
import com.umjari.server.domain.group.service.GroupService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/group")
class GroupController(
    private val groupService: GroupService,
) {
    @PostMapping("/")
    fun createGroup(@Valid @RequestBody createGroupRequest: GroupDto.CreateGroupRequest){
        groupService.createGroup(createGroupRequest)
    }

}
