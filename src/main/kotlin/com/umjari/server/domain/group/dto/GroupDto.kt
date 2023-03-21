package com.umjari.server.domain.group.dto

import com.umjari.server.domain.group.model.Group
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

class GroupDto {
    data class CreateGroupRequest(
        @field:NotBlank val name: String?,
        val logo: String?,
        @field:NotBlank val practiceTime: String?,
        @field:NotNull val audition: Boolean?,
        @field:NotNull @field:PositiveOrZero
        val membershipFee: Int?,
        @field:NotNull @field:PositiveOrZero
        val monthlyFee: Int?,
        @field:NotBlank val regionParent: String?,
        @field:NotBlank val regionChild: String?,
        @field:NotBlank val regionDetail: String?,
        val homepage: String?,
        val detailIntro: String?,
        @field:NotNull
        val recruit: Boolean?,
        val recruitDetail: String?,
    )

    data class UpdateGroupRequest(
        @field:NotBlank val name: String?,
        @field:NotBlank val practiceTime: String?,
        @field:NotNull val audition: Boolean?,
        @field:NotNull @field:PositiveOrZero
        val membershipFee: Int?,
        @field:NotNull @field:PositiveOrZero
        val monthlyFee: Int?,
        @field:NotBlank val regionParent: String?,
        @field:NotBlank val regionChild: String?,
        @field:NotBlank val regionDetail: String?,
        val homepage: String?,
        val detailIntro: String?,
        @field:NotNull
        val recruit: Boolean?,
        val recruitDetail: String?,
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
        val recruitDetail: String?,
    ) {
        constructor(group: Group) : this(
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
            recruitDetail = group.recruitDetail,
        )
    }
}
