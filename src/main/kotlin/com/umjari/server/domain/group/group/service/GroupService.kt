package com.umjari.server.domain.group.group.service

import com.umjari.server.domain.concert.dto.ConcertDto
import com.umjari.server.domain.concert.repository.ConcertRepository
import com.umjari.server.domain.group.group.dto.GroupDto
import com.umjari.server.domain.group.group.dto.GroupRecommendationFilter
import com.umjari.server.domain.group.group.exception.GroupIdNotFoundException
import com.umjari.server.domain.group.group.model.Group
import com.umjari.server.domain.group.group.repository.GroupRepository
import com.umjari.server.domain.group.group.specification.GroupSpecificationBuilder
import com.umjari.server.domain.group.groupmusics.repository.GroupMusicRepository
import com.umjari.server.domain.group.instruments.Instrument
import com.umjari.server.domain.group.members.component.GroupMemberAuthorityValidator
import com.umjari.server.domain.group.members.dto.GroupRegisterDto
import com.umjari.server.domain.group.members.exception.GroupRoleNotAuthorizedException
import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.domain.group.members.repository.GroupMemberRepository
import com.umjari.server.domain.region.service.RegionService
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.service.UserService
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.SimpleDateFormat

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val groupMusicRepository: GroupMusicRepository,
    private val concertRepository: ConcertRepository,
    private val regionService: RegionService,
    private val userService: UserService,
    private val groupMemberAuthorityValidator: GroupMemberAuthorityValidator,
) {
    private final val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

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
            tags = createGroupRequest.tags.joinToString(",", ",", ","),
        ).also { group -> groupRepository.save(group) }

        return GroupDto.GroupDetailResponse(group)
    }

    fun getGroup(groupId: Long, user: User?): GroupDto.GroupDetailResponse {
        val group = groupRepository.findGroupFetchSetList(groupId)
            ?: throw GroupIdNotFoundException(groupId)
        val memberStatus = user?.let {
            groupMemberRepository.findByGroupIdAndUserId(
                groupId = group.id,
                userId = it.id,
            )
        }

        return if (memberStatus != null) {
            GroupDto.GroupDetailResponse(group, memberStatus.role)
        } else {
            GroupDto.GroupDetailResponse(group)
        }
    }

    fun getGroupRecruitDetail(groupId: Long): GroupDto.GroupRecruitDetailResponse {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)
        return GroupDto.GroupRecruitDetailResponse(group)
    }

    fun updateGroup(user: User, groupId: Long, updateGroupRequest: GroupDto.UpdateGroupRequest) {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)

        groupMemberAuthorityValidator.checkMemberAuthorities(GroupMember.MemberRole.ADMIN, groupId, user.id)

        with(group) {
            name = updateGroupRequest.name!!
            logo = updateGroupRequest.logo ?: "default_image"
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
            tags = updateGroupRequest.tags.joinToString(",", ",", ",")
        }

        groupRepository.save(group)
    }

    fun toggleGroupRecruit(user: User, groupId: Long) {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)
        groupMemberAuthorityValidator.checkMemberAuthorities(GroupMember.MemberRole.ADMIN, groupId, user.id)
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
        groupMemberAuthorityValidator.checkMemberAuthorities(GroupMember.MemberRole.ADMIN, groupId, user.id)
        group.recruitInstruments = updateGroupRecruitDetailRequest.recruitInstruments
        group.recruitDetail = updateGroupRecruitDetailRequest.recruitDetail
        groupRepository.save(group)
    }

    fun getConcertListByGroupId(groupId: Long, pageable: Pageable): PageResponse<ConcertDto.ConcertSimpleResponse> {
        if (!groupRepository.existsById(groupId)) {
            throw GroupIdNotFoundException(groupId)
        }
        val concerts = concertRepository.getConcertsByGroupId(groupId, pageable)
            .map { ConcertDto.ConcertSimpleResponse(it) }
        return PageResponse(concerts, pageable.pageNumber)
    }

    fun updateGroupMemberTimestamp(
        user: User,
        groupId: Long,
        updateGroupMemberTimestampRequest: GroupRegisterDto.UpdateGroupMemberTimestampRequest,
    ) {
        val groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, user.id)
            ?: throw GroupRoleNotAuthorizedException(GroupMember.MemberRole.MEMBER)

        with(groupMember) {
            joinedAt = updateGroupMemberTimestampRequest.joinedAt?.let { timestamp ->
                dateFormatter.parse(timestamp)
            }
            leavedAt = updateGroupMemberTimestampRequest.leavedAt?.let { timestamp ->
                dateFormatter.parse(timestamp)
            }
        }
        groupMemberRepository.save(groupMember)
    }

    fun registerGroupMember(
        groupId: Long,
        registerRequest: GroupRegisterDto.GroupRegisterRequest,
        role: GroupMember.MemberRole,
    ): GroupRegisterDto.GroupRegisterResponse {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)

        val requestUserIds = registerRequest.userIds.toSet()

        val (existingUserIds, userMap) = userService.getUserIdToUserMapInUserIds(requestUserIds)
        val alreadyEnrolledUser = groupMemberRepository.findAllAlreadyEnrolled(existingUserIds, groupId)
        val alreadyEnrolledUserMap = alreadyEnrolledUser.associateBy { it.user.userId }
        val objectList = existingUserIds.map { userId ->
            val groupMember = alreadyEnrolledUserMap[userId]
            groupMember?.also { it.role = role }
                ?: GroupMember(
                    group = group,
                    user = userMap.getValue(userId),
                    role = role,
                )
        }

        groupMemberRepository.saveAll(objectList)

        val notExistingUserIds = requestUserIds.subtract(existingUserIds)
        val failedUsers = notExistingUserIds.map {
            GroupRegisterDto.FailedUser(it, "User does not exist.")
        }

        return GroupRegisterDto.GroupRegisterResponse(failedUsers)
    }

    @Transactional
    fun removeGroupMember(
        groupId: Long,
        removeRequest: GroupRegisterDto.GroupRegisterRequest,
    ): GroupRegisterDto.GroupRegisterResponse {
        val userIds = removeRequest.userIds.toSet()
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)

        val (existingUserIds, _) = userService.getUserIdToUserMapInUserIds(userIds)
        val enrolledUser = groupMemberRepository.findAllAlreadyEnrolled(existingUserIds, group.id)
        val enrolledUserIds = enrolledUser.map { it.user.userId }.toSet()

        groupMemberRepository.deleteAll(enrolledUser)

        val notExistingUserIds = userIds.subtract(existingUserIds)
        val failedUsers = mutableListOf<GroupRegisterDto.FailedUser>()
        notExistingUserIds.forEach {
            failedUsers.add(GroupRegisterDto.FailedUser(it, "User does not exist."))
        }

        existingUserIds.filter { userId -> !enrolledUserIds.contains(userId) }
            .forEach { notEnrolledUserId ->
                failedUsers.add(GroupRegisterDto.FailedUser(notEnrolledUserId, "User does not enrolled in group."))
            }
        return GroupRegisterDto.GroupRegisterResponse(failedUsers)
    }

    fun searchGroupList(
        regionParent: String?,
        regionChild: String?,
        name: String?,
        composer: String?,
        musicName: String?,
        instruments: List<Instrument>?,
        tags: List<String>?,
        currentUser: User?,
        pageable: Pageable,
    ): PageResponse<GroupDto.GroupListResponse> {
        val spec = GroupSpecificationBuilder()
        regionParent?.let { if (regionParent != "전체") spec.filteredByRegionParent(regionParent) }
        regionChild?.let { if (regionChild != "전체") spec.filteredByRegionChild(regionChild) }
        name?.let { spec.filteredByName(name) }
        composer?.let { spec.filteredByComposer(composer) }
        musicName?.let { spec.filteredByMusicName(musicName) }
        if (!instruments.isNullOrEmpty()) spec.filteredByRecruitInstruments(instruments)
        if (!tags.isNullOrEmpty()) spec.filteredByTags(tags)
        val groups = groupRepository.findAll(spec.build(), pageable)
        val idSet = groups.map { it.id }.toSet()
        val setListMap = groupMusicRepository.fetchGroupMusicByGroupIds(idSet)
            .groupBy { it.group.id }
        return if (currentUser == null) {
            val groupResponse = groups.map { GroupDto.GroupListResponse(it, setListMap) }
            PageResponse(groupResponse, pageable.pageNumber)
        } else {
            val friendCountList = groupMemberRepository.findFriendCount(idSet, currentUser.id)
            val friendCountMap = friendCountList.associate { it.groupId to it.count }
            val concertResponses = groups.map { GroupDto.GroupListResponse(it, setListMap, friendCountMap[it.id]) }
            PageResponse(concertResponses, pageable.pageNumber)
        }
    }

    fun getRecommendGroupList(
        currentUser: User,
    ): List<GroupDto.GroupRecommendationListResponse> {
        val interestMusicIdList = currentUser.getInterestMusicIdList()

        // 유저 선호곡과 겹치는 곡이 1개 이상인 그룹 찾기
        val interestMusicGroupList = groupMusicRepository.countGroupMusicsByMusicIdIn(interestMusicIdList)
        val groupIdSet = interestMusicGroupList.map { it.groupId }.toSet()

        // 유저 선호곡과 겹치는 곡이 1개 이상인 그룹의 회원 fetch
        val groupMemberCounts = groupRepository.findAllByIdsInWithRegionAndMemberList(groupIdSet)

        // 추천 그룹 group_id 순서
        val recommendationOrderedGroupIdList = GroupRecommendationFilter(
            interestMusicGroupList,
            groupMemberCounts,
            currentUser.region,
        ).getRecommendationGroupIdOrder()

        val groupQueryList = groupRepository.findAllGroupsFetchSetListByIdIn(groupIdSet)
        val groupIdToGroupMap = groupQueryList.associateBy { it.id }
        return recommendationOrderedGroupIdList.map {
            GroupDto.GroupRecommendationListResponse(groupIdToGroupMap.getValue(it))
        }
    }
}
