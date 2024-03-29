package com.umjari.server.domain.group.group.controller

import com.umjari.server.domain.concert.dto.ConcertDto
import com.umjari.server.domain.group.group.dto.GroupDto
import com.umjari.server.domain.group.group.service.GroupService
import com.umjari.server.domain.group.groupmusics.service.GroupMusicService
import com.umjari.server.domain.group.instruments.Instrument
import com.umjari.server.domain.group.members.dto.GroupRegisterDto
import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import com.umjari.server.global.pagination.PageResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Group APIs", description = "그룹 API")
@RestController
@RequestMapping("/api/v1/group")
class GroupController(
    private val groupService: GroupService,
    private val groupMusicService: GroupMusicService,
) {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun createGroup(
        @Valid @RequestBody
        createGroupRequest: GroupDto.CreateGroupRequest,
    ): GroupDto.GroupDetailResponse {
        return groupService.createGroup(createGroupRequest)
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    fun searchGroupList(
        @RequestParam(required = false) regionParent: String? = null,
        @RequestParam(required = false) regionChild: String? = null,
        @RequestParam(required = false) name: String? = null,
        @RequestParam(required = false) composer: String? = null,
        @RequestParam(required = false) musicName: String? = null,
        @RequestParam(required = false) instruments: List<Instrument>? = null,
        @RequestParam(required = false) tags: List<String>? = null,
        @CurrentUser currentUser: User?,
        @PageableDefault(
            size = 20,
            sort = ["name", "regionDetail"],
            direction = Direction.ASC,
        ) pageable: Pageable,
    ): PageResponse<GroupDto.GroupListResponse> {
        return groupService.searchGroupList(
            regionParent,
            regionChild,
            name,
            composer,
            musicName,
            instruments,
            tags,
            currentUser,
            pageable,
        )
    }

    @GetMapping("/recommendation/")
    @ResponseStatus(HttpStatus.OK)
    fun getRecommendGroupList(
        @CurrentUser currentUser: User,
    ): List<GroupDto.GroupRecommendationListResponse> {
        return groupService.getRecommendGroupList(currentUser)
    }

    @GetMapping("/{group_id}/")
    @ResponseStatus(HttpStatus.OK)
    fun getGroup(@PathVariable("group_id") groupId: Long, @CurrentUser user: User?): GroupDto.GroupDetailResponse {
        return groupService.getGroup(groupId, user)
    }

    @GetMapping("/{group_id}/recruit/")
    @ResponseStatus(HttpStatus.OK)
    fun getGroupRecruitDetail(@PathVariable("group_id") groupId: Long): GroupDto.GroupRecruitDetailResponse {
        return groupService.getGroupRecruitDetail(groupId)
    }

    @PutMapping("/{group_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateGroup(
        @PathVariable("group_id") groupId: Long,
        @Valid @RequestBody
        updateGroupRequest: GroupDto.UpdateGroupRequest,
        @CurrentUser user: User,
    ) {
        groupService.updateGroup(user, groupId, updateGroupRequest)
    }

    @PutMapping("/{group_id}/is-recruit/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun toggleGroupRecruit(
        @PathVariable("group_id") groupId: Long,
        @CurrentUser user: User,
    ) {
        groupService.toggleGroupRecruit(user, groupId)
    }

    @PutMapping("/{group_id}/recruit-detail/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateGroupRecruitDetail(
        @PathVariable("group_id") groupId: Long,
        @Valid @RequestBody
        updateGroupRecruitDetailRequest: GroupDto.UpdateGroupRecruitDetailRequest,
        @CurrentUser user: User,
    ) {
        groupService.updateGroupRecruitDetail(user, groupId, updateGroupRecruitDetailRequest)
    }

    @GetMapping("/{group_id}/concerts/")
    @ResponseStatus(HttpStatus.OK)
    fun getConcertListByGroupId(
        @PathVariable("group_id") groupId: Long,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Direction.DESC,
        ) pageable: Pageable,
    ): PageResponse<ConcertDto.ConcertSimpleResponse> {
        return groupService.getConcertListByGroupId(groupId, pageable)
    }

    @PutMapping("/{group_id}/register/timestamp/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateRegisterTimestamp(
        @PathVariable("group_id") groupId: Long,
        @Valid @RequestBody
        updateGroupMemberTimestampRequest: GroupRegisterDto.UpdateGroupMemberTimestampRequest,
        @CurrentUser
        user: User,
    ) {
        groupService.updateGroupMemberTimestamp(user, groupId, updateGroupMemberTimestampRequest)
    }

    @PostMapping("/{group_id}/register/")
    @ResponseStatus(HttpStatus.OK)
    fun registerGroupMember(
        @PathVariable("group_id") groupId: Long,
        @Valid @RequestBody
        registerRequest: GroupRegisterDto.GroupRegisterRequest,
    ): GroupRegisterDto.GroupRegisterResponse {
        return groupService.registerGroupMember(groupId, registerRequest, GroupMember.MemberRole.MEMBER)
    }

    @DeleteMapping("/{group_id}/register/")
    @ResponseStatus(HttpStatus.OK)
    fun removeGroupMember(
        @PathVariable("group_id") groupId: Long,
        @Valid @RequestBody
        deleteRequest: GroupRegisterDto.GroupRegisterRequest,
    ): GroupRegisterDto.GroupRegisterResponse {
        return groupService.removeGroupMember(groupId, deleteRequest)
    }

    @PostMapping("/{group_id}/register/admin/")
    @ResponseStatus(HttpStatus.OK)
    fun registerGroupMemberAsAdmin(
        @PathVariable("group_id") groupId: Long,
        @Valid @RequestBody
        registerRequest: GroupRegisterDto.GroupRegisterRequest,
    ): GroupRegisterDto.GroupRegisterResponse {
        return groupService.registerGroupMember(groupId, registerRequest, GroupMember.MemberRole.ADMIN)
    }

    @PutMapping("/{group_id}/set-list/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateGroupSetList(
        @PathVariable("group_id") groupId: Long,
        @Valid @RequestBody
        updateGroupSetListRequest: GroupDto.UpdateGroupSetListRequest,
        @CurrentUser user: User,
    ) {
        groupMusicService.updateConcertSetList(user, groupId, updateGroupSetListRequest)
    }
}
