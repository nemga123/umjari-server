package com.umjari.server.domain.group.dto

import com.umjari.server.domain.group.model.Group
import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.model.Instrument
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

class GroupDto {
    data class CreateGroupRequest(
        @field:NotBlank
        @field:Size(max = 255)
        val name: String?,
        @field:Size(max = 255) val logo: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val practiceTime: String?,
        @field:NotNull
        val audition: Boolean?,
        @field:NotNull @field:PositiveOrZero
        val membershipFee: Int?,
        @field:NotNull @field:PositiveOrZero
        val monthlyFee: Int?,
        @field:NotBlank
        @field:Size(max = 255)
        val regionParent: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val regionChild: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val regionDetail: String?,
        @field:Size(max = 255) val homepage: String?,
        @field:Size(max = 255) val detailIntro: String?,
    )

    data class UpdateGroupRequest(
        @field:NotBlank
        @field:Size(max = 255)
        val name: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val practiceTime: String?,
        @field:NotNull
        val audition: Boolean?,
        @field:NotNull @field:PositiveOrZero
        val membershipFee: Int?,
        @field:NotNull @field:PositiveOrZero
        val monthlyFee: Int?,
        @field:NotBlank
        @field:Size(max = 255)
        val regionParent: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val regionChild: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val regionDetail: String?,
        @field:Size(max = 255) val homepage: String?,
        @field:Size(max = 255) val detailIntro: String?,
    )

    data class UpdateGroupRecruitDetailRequest(
        val recruitInstruments: ArrayList<Instrument>,
        val recruitDetail: String,
    )

    data class GroupDetailResponse(
        val id: Long,
        val name: String,
        val logo: String,
        val practiceTime: String,
        val audition: Boolean,
        val membershipFee: Int,
        val monthlyFee: Int,
        val region: String,
        val regionDetail: String,
        val homepage: String?,
        val detailIntro: String?,
        val recruit: Boolean,
        val memberType: String,
    ) {
        constructor(group: Group, memberType: GroupMember.MemberRole) : this(
            id = group.id,
            name = group.name,
            logo = group.logo,
            practiceTime = group.practiceTime,
            audition = group.audition,
            membershipFee = group.membershipFee,
            monthlyFee = group.monthlyFee,
            region = group.region.toString(),
            regionDetail = group.regionDetail,
            homepage = group.homepage,
            detailIntro = group.detailIntro,
            recruit = group.recruit,
            memberType = memberType.toString(),
        )
    }

    data class GroupRecruitDetailResponse(
        val id: Long,
        val recruit: Boolean,
        val recruitInstruments: List<Instrument>?,
        val recruitDetail: String?,
    ) {
        constructor(group: Group) : this(
            id = group.id,
            recruit = group.recruit,
            recruitInstruments = if (group.recruit) group.recruitInstruments else null,
            recruitDetail = if (group.recruit) group.recruitDetail else null,
        )
    }

    data class GroupUserResponse(
        val groupId: Long,
        val groupName: String,
        val joinedAt: String,
        val memberType: String,
    ) {
        constructor(groupMember: GroupMember) : this(
            groupId = groupMember.group.id,
            groupName = groupMember.group.name,
            joinedAt = groupMember.createdAt.toString(),
            memberType = groupMember.role.toString(),
        )
    }
}
