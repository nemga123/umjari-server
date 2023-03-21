package com.umjari.server.domain.group.service

import com.umjari.server.domain.group.dto.GroupDto
import com.umjari.server.domain.group.exception.GroupIdNotFoundException
import com.umjari.server.domain.group.model.Group
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.region.model.Region
import com.umjari.server.domain.region.repository.RegionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val regionRepository: RegionRepository,
) {
    fun createGroup(createGroupRequest: GroupDto.CreateGroupRequest): GroupDto.GroupDetailResponse {
        val region = regionRepository.findByParentAndChild(
            createGroupRequest.regionParent!!,
            createGroupRequest.regionChild!!,
        ) ?: run {
            val obj = Region(createGroupRequest.regionParent, createGroupRequest.regionChild)
            regionRepository.save(obj)
        }
        print("?")
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
            recruit = createGroupRequest.recruit!!,
            recruitDetail = createGroupRequest.recruitDetail,
        )

        groupRepository.save(group)
        return GroupDto.GroupDetailResponse(group)
    }

    fun getGroup(groupId: Long): GroupDto.GroupDetailResponse {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)
        return GroupDto.GroupDetailResponse(group)
    }

    fun updateGroup(groupId: Long, updateGroupRequest: GroupDto.UpdateGroupRequest) {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)
        print("?")

        updateRegionOfGroup(group, updateGroupRequest.regionParent!!, updateGroupRequest.regionChild!!)
        with(group) {
            name = updateGroupRequest.name!!
            practiceTime = updateGroupRequest.practiceTime!!
            audition = updateGroupRequest.audition!!
            membershipFee = updateGroupRequest.membershipFee!!
            print(monthlyFee)
            monthlyFee = updateGroupRequest.monthlyFee!!
            regionDetail = updateGroupRequest.regionDetail!!
            homepage = updateGroupRequest.homepage
            detailIntro = updateGroupRequest.detailIntro
            recruit = updateGroupRequest.recruit!!
            recruitDetail = updateGroupRequest.recruitDetail
        }

        groupRepository.save(group)
    }

    private fun updateRegionOfGroup(group: Group, regionParent: String, regionChild: String) {
        if (group.region.parent != regionParent && group.region.child != regionChild) {
            val region = regionRepository.findByParentAndChild(regionParent, regionChild) ?: run {
                val obj = Region(regionParent, regionChild)
                regionRepository.save(obj)
            }
            group.region = region
        }
    }
}
