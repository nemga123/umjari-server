package com.umjari.server.domain.group.service

import com.umjari.server.domain.concert.dto.ConcertDto
import com.umjari.server.domain.concert.repository.ConcertRepository
import com.umjari.server.domain.group.dto.GroupDto
import com.umjari.server.domain.group.dto.GroupRegisterDto
import com.umjari.server.domain.group.exception.GroupIdNotFoundException
import com.umjari.server.domain.group.model.Group
import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.repository.GroupMemberRepository
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.region.service.RegionService
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val concertRepository: ConcertRepository,
    private val regionService: RegionService,
    private val userRepository: UserRepository,
    private val groupMemberAuthorityService: GroupMemberAuthorityService,
) {
    fun createGroup(createGroupRequest: GroupDto.CreateGroupRequest): GroupDto.GroupDetailResponse {
        val region = regionService.getOrCreateRegion(
            createGroupRequest.regionParent!!,
            createGroupRequest.regionChild!!,
        )

        val group = Group(
            name = createGroupRequest.name!!,
            logo = createGroupRequest.logo ?: "default_image",
            practiceTime = createGroupRequest.practiceTime!!,
            audition = createGroupRequest.audition!!,
            membershipFee = createGroupRequest.membershipFee!!,
            monthlyFee = createGroupRequest.monthlyFee!!,
            region = region,
            regionDetail = createGroupRequest.regionDetail!!,
            homepage = createGroupRequest.homepage,
            detailIntro = createGroupRequest.detailIntro,
        )

        groupRepository.save(group)
        return GroupDto.GroupDetailResponse(group, GroupMember.MemberRole.NON_MEMBER)
    }

    fun getGroup(groupId: Long, user: User?): GroupDto.GroupDetailResponse {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)
        val memberStatus = user?.let {
            groupMemberRepository.findByGroup_IdAndUser_Id(
                groupId = group.id,
                userId = it.id,
            )
        }
        val memberType = memberStatus?.let { it.role } ?: GroupMember.MemberRole.NON_MEMBER
        return GroupDto.GroupDetailResponse(group, memberType)
    }

    fun getGroupRecruitDetail(groupId: Long): GroupDto.GroupRecruitDetailResponse {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)
        return GroupDto.GroupRecruitDetailResponse(group)
    }

    fun updateGroup(user: User, groupId: Long, updateGroupRequest: GroupDto.UpdateGroupRequest) {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)

        groupMemberAuthorityService.checkMemberAuthorities(GroupMember.MemberRole.ADMIN, groupId, user.id)

        with(group) {
            name = updateGroupRequest.name!!
            practiceTime = updateGroupRequest.practiceTime!!
            audition = updateGroupRequest.audition!!
            membershipFee = updateGroupRequest.membershipFee!!
            monthlyFee = updateGroupRequest.monthlyFee!!
            regionDetail = updateGroupRequest.regionDetail!!
            homepage = updateGroupRequest.homepage
            detailIntro = updateGroupRequest.detailIntro
            region = regionService.getOrCreateRegion(
                updateGroupRequest.regionParent!!,
                updateGroupRequest.regionChild!!,
            )
        }

        groupRepository.save(group)
    }

    fun toggleGroupRecruit(user: User, groupId: Long) {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)
        groupMemberAuthorityService.checkMemberAuthorities(GroupMember.MemberRole.ADMIN, groupId, user.id)
        group.recruit = !group.recruit
        groupRepository.save(group)
    }

    fun updateGroupRecruitDetail(
        user: User,
        groupId: Long,
        updateGroupRecruitDetailRequest: GroupDto.UpdateGroupRecruitDetailRequest,
    ) {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)
        groupMemberAuthorityService.checkMemberAuthorities(GroupMember.MemberRole.ADMIN, groupId, user.id)
        group.recruitInstruments = updateGroupRecruitDetailRequest.recruitInstruments
        group.recruitDetail = updateGroupRecruitDetailRequest.recruitDetail
        groupRepository.save(group)
    }

    fun getConcertListByGroupId(groupId: Long, pageable: Pageable): PageResponse<ConcertDto.ConcertSimpleResponse> {
        if (!groupRepository.existsById(groupId)) {
            throw GroupIdNotFoundException(groupId)
        }
        val concerts = concertRepository.getConcertsByGroupId(groupId, pageable)
        val concertResponses = concerts.map { ConcertDto.ConcertSimpleResponse(it) }
        return PageResponse(concertResponses, pageable.pageNumber)
    }

    fun registerGroupMember(
        groupId: Long,
        registerRequest: GroupRegisterDto.GroupRegisterRequest,
        role: GroupMember.MemberRole,
    ): GroupRegisterDto.GroupRegisterResponse {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)

        val requestUserIds = registerRequest.userIds.toMutableList()

        val failedUsers = mutableListOf<GroupRegisterDto.FailedUser>()
        val existingUserIds = userRepository.findUserIdsByUserIdIn(requestUserIds)
        val notEnrolledUsers = if (existingUserIds.isNotEmpty()) {
            groupMemberRepository.findAllUserIdsNotEnrolled(
                existingUserIds,
                groupId,
            )
        } else {
            emptySet()
        }
        val notEnrolledUserIds = notEnrolledUsers.map { it.userId }.toSet()
        notEnrolledUsers.forEach { user ->
            val groupMember = GroupMember(group = group, user = user, role = role)
            groupMemberRepository.save(groupMember)
        }

        val notExistingUserIds = requestUserIds.subtract(existingUserIds)
        notExistingUserIds.forEach {
            failedUsers.add(GroupRegisterDto.FailedUser(it, "User does not exist."))
        }

        val alreadyEnrolledUserIds = existingUserIds.subtract(notEnrolledUserIds)
        alreadyEnrolledUserIds.forEach {
            failedUsers.add(GroupRegisterDto.FailedUser(it, "User is already enrolled."))
        }

        return GroupRegisterDto.GroupRegisterResponse(failedUsers)
    }
}
