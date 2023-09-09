package com.umjari.server.domain.group.group.dto

import com.umjari.server.domain.group.group.model.Group
import com.umjari.server.domain.group.groupmusics.dto.GroupMusicDto

class GroupRecommendationFilter(
    interestMusicGroupList: List<GroupMusicDto.CountInterestMusicGroupQueryResultInterface>,
    groupMemberCounts: List<Group>,
    userRegion: String,
) {
    private var groupList: List<GroupRecommendationFactor>

    private data class GroupRecommendationFactor(
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
        val userRegionParent = userRegion.split(" ")[0]
        val userRegionChild = userRegion.split(" ")[1]
        groupList = interestMusicGroupList.map {
            val group = groupIdToGroupMap.getValue(it.groupId)
            GroupRecommendationFactor(
                groupId = it.groupId,
                interestMusicCount = it.countMusic,
                memberCount = group.members.size,
                regionStatus = if (userRegionParent == group.region.parent && userRegionChild == group.region.child) {
                    RegionMatchStatus.MATCH_ALL
                } else if (userRegionParent == group.region.parent) {
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
