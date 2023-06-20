package com.umjari.server.domain.concert.dto

import com.umjari.server.domain.concert.model.Concert
import com.umjari.server.domain.concert.model.ConcertMusic
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.text.SimpleDateFormat
import java.util.*

class ConcertDto {
    data class CreateConcertRequest(
        @field:NotBlank
        @field:Size(max = 255)
        val title: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val subtitle: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val conductor: String?,
        @field:NotNull
        @field:Size(max = 255)
        val solist: String?,
        @field:NotNull
        @field:Size(max = 255)
        val host: String?,
        @field:NotNull
        @field:Size(max = 255)
        val support: String?,
        @field:NotNull
        @field:Size(max = 255)
        val qna: String?,
        @field:NotBlank
        val concertInfo: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val posterImg: String?,
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
        @field:NotBlank
        @field:Size(max = 255)
        val regionParent: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val regionChild: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val regionDetail: String?,
        val musicIds: ArrayList<Long> = arrayListOf(),
    )

    data class UpdateConcertDetailRequest(
        @field:NotBlank
        @field:Size(max = 255)
        val title: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val subtitle: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val conductor: String?,
        @field:NotNull
        @field:Size(max = 255)
        val solist: String?,
        @field:NotNull
        @field:Size(max = 255)
        val host: String?,
        @field:NotNull
        @field:Size(max = 255)
        val support: String?,
        @field:NotNull
        @field:Size(max = 255)
        val qna: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val posterImg: String?,
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
        @field:NotBlank
        @field:Size(max = 255)
        val regionParent: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val regionChild: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val regionDetail: String?,
    )

    data class UpdateConcertInfoRequest(
        @field:NotBlank
        val concertInfo: String?,
    )

    data class UpdateConcertSetListRequest(
        val musicIds: ArrayList<Long> = arrayListOf(),
    )

    data class ConcertDetailResponse(
        val id: Long,
        val groupId: Long,
        val title: String,
        val subtitle: String,
        val conductor: String,
        val solist: String,
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
        val setList: List<ConcertMusicDto.ConcertSetResponse>,
    ) {
        constructor(concert: Concert, playList: List<ConcertMusic>) : this(
            id = concert.id,
            groupId = concert.group.id,
            title = concert.title,
            subtitle = concert.subtitle,
            conductor = concert.conductor,
            solist = concert.solist,
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
            setList = playList.map { ConcertMusicDto.ConcertSetResponse(it) },
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
        val concertRunningTime: Int,
        val fee: Int,
        val region: String,
        val regionDetail: String,
        val setList: List<ConcertMusicDto.ConcertSetResponse>,
    ) {
        constructor(concert: Concert) : this(
            id = concert.id,
            groupId = concert.group.id,
            title = concert.title,
            subtitle = concert.subtitle,
            posterImg = concert.posterImg,
            concertDate = SimpleDateFormat("yyyy-MM-dd").format(concert.concertDate),
            concertTime = SimpleDateFormat("HH:mm:ss").format(concert.concertDate),
            concertRunningTime = concert.concertRunningTime,
            fee = concert.fee,
            region = concert.region.toString(),
            regionDetail = concert.regionDetail,
            setList = concert.playList.sortedBy { it.id }.map { ConcertMusicDto.ConcertSetResponse(it) },
        )
    }

    data class ConcertDashboardResponse(
        val id: Long,
        val groupId: Long,
        val title: String,
        val subtitle: String,
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
            posterImg = concert.posterImg,
            concertDate = SimpleDateFormat("yyyy-MM-dd").format(concert.concertDate),
            concertTime = SimpleDateFormat("HH:mm:ss").format(concert.concertDate),
            concertRunningTime = concert.concertRunningTime,
            fee = concert.fee,
            region = concert.region.toString(),
            regionDetail = concert.regionDetail,
        )
    }
}
