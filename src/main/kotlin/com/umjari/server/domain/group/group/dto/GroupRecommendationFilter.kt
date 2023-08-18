package com.umjari.server.domain.group.group.dto

import com.umjari.server.domain.group.group.model.Group
import com.umjari.server.domain.group.groupmusics.dto.GroupMusicDto
import com.umjari.server.domain.region.model.Region

class GroupRecommendationFilter(
    interestMusicGroupList: List<GroupMusicDto.CountInterestMusicGroupQueryResultInterface>,
    groupMemberCounts: List<Group>,
    userRegion: Region?,
) {
    private var groupList: List<GroupRecommendationFactor>

    data class GroupRecommendationFactor(
        val groupId: Long,
        val interestMusicCount: Int,
        val memberCount: Int,
        val regionStatus: RegionMatchStatus,
    )

    enum class RegionMatchStatus {
        NOT_MATCH, MATCH_REGION_PARENT, MATCH_ALL,
    }

    init {
        val groupIdToGroupMap = groupMemberCounts.associateBy { it.id }
        groupList = interestMusicGroupList.map {
            val group = groupIdToGroupMap.getValue(it.groupId)
            GroupRecommendationFactor(
                groupId = it.groupId,
                interestMusicCount = it.countMusic,
                memberCount = group.members.size,
                regionStatus = if (userRegion == null) {
                    RegionMatchStatus.NOT_MATCH
                } else if (userRegion.parent == group.region.parent && userRegion.child == group.region.child) {
                    RegionMatchStatus.MATCH_ALL
                } else if (userRegion.parent == group.region.parent) {
                    RegionMatchStatus.MATCH_REGION_PARENT
                } else {
                    RegionMatchStatus.NOT_MATCH
                },
            )
        }
    }

    fun getRecommendationGroupIdOrder() = groupList.sortedWith(
        compareByDescending<GroupRecommendationFactor> { it.interestMusicCount }
            .thenByDescending { it.regionStatus }
            .thenByDescending { it.memberCount }
            .thenBy { it.groupId },
    ).take(5).map { it.groupId }
}
