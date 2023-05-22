package com.umjari.server.domain.concert.dto

import com.umjari.server.domain.concert.model.ConcertParticipant
import com.umjari.server.domain.user.dto.UserDto
import com.umjari.server.domain.user.model.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.Date

class ConcertParticipantDto {
    data class RegisterConcertParticipantListRequest(
        val participantList: List<RegisterConcertParticipantRequest>,
    )

    data class RegisterConcertParticipantRequest(
        @field:NotBlank
        @field:Size(max = 255)
        val userId: String,
        @field:NotBlank
        @field:Size(max = 255)
        val part: String,
        @field:NotBlank
        @field:Size(max = 255)
        val detailPart: String,
        @field:NotNull
        val role: ConcertParticipant.ParticipantRole,
    )

    data class RemoveConcertParticipantListRequest(
        val userIds: List<String>,
    )

    data class RegisterConcertParticipantsResponse(
        val failedUsers: List<FailedUser>,
    )

    data class FailedUser(
        val userId: String,
        val reason: String,
    )

    data class ConcertParticipantsListResponse(
        val participants: List<ConcertParticipantsByPartResponse>,
    )

    data class ConcertParticipantsByPartResponse(
        val part: String,
        val master: MutableList<UserDto.SimpleUserDto> = mutableListOf(),
        val principal: MutableList<UserDto.SimpleUserDto> = mutableListOf(),
        val assistantPrincipal: MutableList<UserDto.SimpleUserDto> = mutableListOf(),
        val member: MutableList<UserDto.SimpleUserDto> = mutableListOf(),
    ) {
        fun add(concertParticipantInterface: ConcertParticipantSqlSimpleInterface) {
            add(concertParticipantInterface.performer, concertParticipantInterface.role)
        }

        fun add(concertParticipant: ConcertParticipant) {
            add(concertParticipant.performer, concertParticipant.role)
        }

        private fun add(performer: User, role: ConcertParticipant.ParticipantRole) {
            when (role) {
                ConcertParticipant.ParticipantRole.MASTER -> master.add(
                    UserDto.SimpleUserDto(performer),
                )
                ConcertParticipant.ParticipantRole.PRINCIPAL -> principal.add(
                    UserDto.SimpleUserDto(performer),
                )
                ConcertParticipant.ParticipantRole.ASSISTANT_PRINCIPAL -> assistantPrincipal.add(
                    UserDto.SimpleUserDto(performer),
                )
                ConcertParticipant.ParticipantRole.MEMBER -> member.add(
                    UserDto.SimpleUserDto(performer),
                )
            }
        }
    }

    interface ConcertParticipantSqlSimpleInterface {
        val performer: User
        val role: ConcertParticipant.ParticipantRole
        val part: String
    }

    interface ConcertPartSqlSimpleInterface {
        val id: Long
        val shortComposerEng: String
        val nameEng: String
        val part: String
        val detailPart: String
        val groupName: String
    }

    data class ParticipatedConcertResponse(
        val id: Long,
        val shortComposerEng: String,
        val nameEng: String,
        val part: String,
        val detailPart: String,
        val groupName: String,
    ) {
        constructor(concertPart: ConcertPartSqlSimpleInterface) : this(
            id = concertPart.id,
            shortComposerEng = concertPart.shortComposerEng,
            nameEng = concertPart.nameEng,
            part = concertPart.part,
            detailPart = concertPart.detailPart,
            groupName = concertPart.groupName,
        )
    }

    data class ParticipatedConcertListResponse(
        val participatedConcerts: List<ParticipatedConcertResponse>,
    )

    interface ConcertPartSqlWithImageInterface : ConcertPartSqlSimpleInterface {
        val concertPoster: String
        val subtitle: String
        val concertDate: Date
        val regionDetail: String
    }

    data class ParticipatedConcertSimpleResponse(
        val shortComposerEng: String,
        val nameEng: String,
        val part: String,
        val detailPart: String,
        val groupName: String,
    ) {
        constructor(concertPart: ConcertPartSqlWithImageInterface) : this(
            shortComposerEng = concertPart.shortComposerEng,
            nameEng = concertPart.nameEng,
            part = concertPart.part,
            detailPart = concertPart.detailPart,
            groupName = concertPart.groupName,
        )
    }

    data class ParticipatedConcertsGroupByConcertIdResponse(
        val id: Long,
        val concertPoster: String,
        val subtitle: String,
        val concertDate: String,
        val regionDetail: String,
        val participatedList: List<ParticipatedConcertSimpleResponse>,
    ) {
        constructor(
            concertId: Long,
            participatedList: List<ConcertPartSqlWithImageInterface>,
        ) : this(
            id = concertId,
            concertPoster = participatedList[0].concertPoster,
            subtitle = participatedList[0].subtitle,
            concertDate = participatedList[0].concertDate.toString(),
            regionDetail = participatedList[0].regionDetail,
            participatedList = participatedList.map { ParticipatedConcertSimpleResponse(it) },
        )
    }

    data class ParticipatedConcertsGroupByConcertIdListResponse(
        val participatedConcerts: List<ParticipatedConcertsGroupByConcertIdResponse>,
    ) {
        constructor(listGroupByConcertId: Map<Long, List<ConcertPartSqlWithImageInterface>>) : this(
            participatedConcerts = listGroupByConcertId.map { (id, list) ->
                ParticipatedConcertsGroupByConcertIdResponse(
                    concertId = id,
                    participatedList = list,
                )
            },
        )
    }
}
