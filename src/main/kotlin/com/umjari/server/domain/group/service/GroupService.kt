package com.umjari.server.domain.group.service

import com.umjari.server.domain.group.dto.GroupDto
import com.umjari.server.domain.group.model.Group
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.region.model.Region
import com.umjari.server.domain.region.repository.RegionRepository
import org.springframework.stereotype.Service

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val regionRepository: RegionRepository,
) {
    fun createGroup(createGroupRequest: GroupDto.CreateGroupRequest){
        val region = regionRepository.findByParentAndChild(
            createGroupRequest.regionParent,
            createGroupRequest.regionChild,
        ) ?: run{
            val obj = Region(createGroupRequest.regionParent, createGroupRequest.regionChild)
            regionRepository.save(obj)
        }

        val group = Group(
            name = createGroupRequest.name,
            logo = createGroupRequest.logo,
            practiceTime = createGroupRequest.practiceTime,
            audition = createGroupRequest.audition,
            membershipFee = createGroupRequest.membershipFee,
            monthlyFee = createGroupRequest.monthlyFee,
            region = region,
            regionDetail = createGroupRequest.regionDetail,
            homepage = createGroupRequest.homepage,
            detailIntro = createGroupRequest.detailIntro,
            recruit = createGroupRequest.recruit,
            recruitDetail = createGroupRequest.recruitDetail,
        )

        groupRepository.save(group)
    }
}
