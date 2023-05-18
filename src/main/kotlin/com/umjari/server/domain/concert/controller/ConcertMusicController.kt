package com.umjari.server.domain.concert.controller

import com.umjari.server.domain.concert.dto.ConcertParticipantDto
import com.umjari.server.domain.concert.service.ConcertMusicService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Concert SetList APIs", description = "콘서트에서 연주되는 음악 무대 API")
@RestController
@RequestMapping("/api/v1/concert/{concert_id}/concert-music")
class ConcertMusicController(
    private val concertMusicService: ConcertMusicService,
) {
    @GetMapping("/{concert_music_id}/participant/")
    @ResponseStatus(HttpStatus.OK)
    fun getConcertParticipantsList(
        @PathVariable("concert_id") concertId: Long,
        @PathVariable("concert_music_id") concertMusicId: Long,
        @CurrentUser user: User?,
    ): ConcertParticipantDto.ConcertParticipantsListResponse {
        return concertMusicService.getConcertParticipantsList(user, concertId, concertMusicId)
    }

    @PutMapping("/{concert_music_id}/participant/")
    @ResponseStatus(HttpStatus.OK)
    fun registerConcertParticipants(
        @PathVariable("concert_id") concertId: Long,
        @PathVariable("concert_music_id") concertMusicId: Long,
        @Valid @RequestBody
        registerConcertParticipantListRequest: ConcertParticipantDto.RegisterConcertParticipantListRequest,
        @CurrentUser user: User,
    ): ConcertParticipantDto.RegisterConcertParticipantsResponse {
        return concertMusicService.registerConcertParticipant(
            user,
            concertId,
            concertMusicId,
            registerConcertParticipantListRequest,
        )
    }

    @DeleteMapping("/{concert_music_id}/participant/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeConcertParticipants(
        @PathVariable("concert_id") concertId: Long,
        @PathVariable("concert_music_id") concertMusicId: Long,
        @Valid @RequestBody
        removeConcertParticipantListRequest: ConcertParticipantDto.RemoveConcertParticipantListRequest,
        @CurrentUser user: User,
    ) {
        concertMusicService.removeConcertParticipant(
            user,
            concertId,
            concertMusicId,
            removeConcertParticipantListRequest,
        )
    }
}
