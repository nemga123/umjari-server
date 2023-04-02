package com.umjari.server.domain.concert.dto

import com.umjari.server.domain.concert.model.Concert
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.text.SimpleDateFormat
import java.util.*

class ConcertDto {
    data class CreateConcertRequest(
        @field:NotBlank val title: String?,
        @field:NotBlank val subtitle: String?,
        @field:NotBlank val conductor: String?,
        @field:NotNull val host: String?,
        @field:NotNull val support: String?,
        @field:NotNull val qna: String?,
        @field:NotBlank val concertInfo: String?,
        @field:NotBlank val posterImg: String?,
        @field:NotBlank
        @field:Pattern(
            regexp = "^(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})$",
            message = "date format is 'YYYY-MM-DD HH:MM:SS'",
        )
        val concertDate: String?,
        @field:NotNull @field:Positive
        val concertRunningTime: Int?,
        @field:NotNull @field:PositiveOrZero
        val fee: Int?,
        @field:NotBlank val regionParent: String?,
        @field:NotBlank val regionChild: String?,
        @field:NotBlank val regionDetail: String?,
    )

    data class UpdateConcertDetailRequest(
        @field:NotBlank val title: String?,
        @field:NotBlank val subtitle: String?,
        @field:NotBlank val conductor: String?,
        @field:NotNull val host: String?,
        @field:NotNull val support: String?,
        @field:NotNull val qna: String?,
        @field:NotBlank val posterImg: String?,
        @field:NotBlank
        @field:Pattern(
            regexp = "^(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})$",
            message = "date format is 'YYYY-MM-DD HH:MM:SS'",
        )
        val concertDate: String?,
        @field:NotNull @field:Positive
        val concertRunningTime: Int?,
        @field:NotNull @field:PositiveOrZero
        val fee: Int?,
        @field:NotBlank val regionParent: String?,
        @field:NotBlank val regionChild: String?,
        @field:NotBlank val regionDetail: String?,
    )

    data class UpdateConcertInfoRequest(
        @field:NotBlank val concertInfo: String?,
    )

    data class ConcertDetailResponse(
        val id: Long,
        val groupId: Long,
        val title: String,
        val subtitle: String,
        val conductor: String,
        val host: String,
        val support: String,
        val qna: String,
        val concertInfo: String,
        val posterImg: String,
        val concertDate: String,
        val concertTime: String,
        val concertRunningTime: Int,
        val fee: Int,
        val region: String,
        val regionDetail: String,
    ) {
        constructor(concert: Concert) : this(
            id = concert.id,
            groupId = concert.group.id,
            title = concert.title,
            subtitle = concert.subtitle,
            conductor = concert.conductor,
            host = concert.host,
            support = concert.support,
            qna = concert.qna,
            concertInfo = concert.concertInfo,
            posterImg = concert.posterImg,
            concertDate = SimpleDateFormat("yyyy-MM-dd").format(concert.concertDate),
            concertTime = SimpleDateFormat("HH:mm:ss").format(concert.concertDate),
            concertRunningTime = concert.concertRunningTime,
            fee = concert.fee,
            region = concert.region.toString(),
            regionDetail = concert.regionDetail,
        )
    }

    data class ConcertSimpleResponse(
        val id: Long,
        val groupId: Long,
        val title: String,
        val subtitle: String,
        val posterImg: String,
        val concertDate: String,
        val concertTime: String,
        val fee: Int,
        val region: String,
        val regionDetail: String,
    ) {
        constructor(concert: Concert) : this(
            id = concert.id,
            groupId = concert.group.id,
            title = concert.title,
            subtitle = concert.subtitle,
            posterImg = concert.posterImg,
            concertDate = SimpleDateFormat("yyyy-MM-dd").format(concert.concertDate),
            concertTime = SimpleDateFormat("HH:mm:ss").format(concert.concertDate),
            fee = concert.fee,
            region = concert.region.toString(),
            regionDetail = concert.regionDetail,
        )
    }
}