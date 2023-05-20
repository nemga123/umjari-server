package com.umjari.server.domain.concert.dto

import com.umjari.server.domain.concert.model.ConcertParticipant
import com.umjari.server.domain.user.dto.UserDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

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
        fun add(concertParticipant: ConcertParticipant) {
            when (concertParticipant.role) {
                ConcertParticipant.ParticipantRole.MASTER -> master.add(
                    UserDto.SimpleUserDto(concertParticipant.performer),
                )
                ConcertParticipant.ParticipantRole.PRINCIPAL -> principal.add(
                    UserDto.SimpleUserDto(concertParticipant.performer),
                )
                ConcertParticipant.ParticipantRole.ASSISTANT_PRINCIPAL -> assistantPrincipal.add(
                    UserDto.SimpleUserDto(concertParticipant.performer),
                )
                ConcertParticipant.ParticipantRole.MEMBER -> member.add(
                    UserDto.SimpleUserDto(concertParticipant.performer),
                )
            }
        }
    }
}
