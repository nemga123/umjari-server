package com.umjari.server.domain.concert.service

import com.umjari.server.domain.concert.dto.ConcertDto
import com.umjari.server.domain.concert.dto.ConcertParticipantDto
import com.umjari.server.domain.concert.exception.ConcertNotFoundException
import com.umjari.server.domain.concert.model.Concert
import com.umjari.server.domain.concert.model.ConcertMusic
import com.umjari.server.domain.concert.repository.ConcertMusicRepository
import com.umjari.server.domain.concert.repository.ConcertParticipantRepository
import com.umjari.server.domain.concert.repository.ConcertRepository
import com.umjari.server.domain.concert.specification.ConcertSpecification
import com.umjari.server.domain.group.group.exception.GroupIdNotFoundException
import com.umjari.server.domain.group.group.repository.GroupRepository
import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.domain.group.members.service.GroupMemberAuthorityService
import com.umjari.server.domain.music.exception.MusicIdNotFoundException
import com.umjari.server.domain.music.repository.MusicRepository
import com.umjari.server.domain.region.service.RegionService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.SimpleDateFormat

@Service
class ConcertService(
    private val concertRepository: ConcertRepository,
    private val concertMusicRepository: ConcertMusicRepository,
    private val concertParticipantRepository: ConcertParticipantRepository,
    private val musicRepository: MusicRepository,
    private val regionService: RegionService,
    private val groupRepository: GroupRepository,
    private val groupMemberAuthorityService: GroupMemberAuthorityService,
) {
    private final val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private final val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

    @Transactional
    fun createConcert(
        user: User,
        createConcertRequest: ConcertDto.CreateConcertRequest,
        groupId: Long,
    ): ConcertDto.ConcertDetailResponse {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)

        groupMemberAuthorityService.checkMemberAuthorities(GroupMember.MemberRole.ADMIN, groupId, user.id)

        val region = regionService.getOrCreateRegion(
            createConcertRequest.regionParent!!,
            createConcertRequest.regionChild!!,
        )

        val concert = Concert(
            title = createConcertRequest.title!!,
            subtitle = createConcertRequest.subtitle!!.trim(),
            conductor = createConcertRequest.conductor!!,
            solist = createConcertRequest.solist!!,
            host = createConcertRequest.host!!,
            support = createConcertRequest.support!!,
            qna = createConcertRequest.qna!!,
            concertInfo = createConcertRequest.concertInfo!!,
            posterImg = createConcertRequest.posterImg!!,
            concertDate = dateTimeFormatter.parse(createConcertRequest.concertDate!!),
            concertRunningTime = createConcertRequest.concertRunningTime!!,
            fee = createConcertRequest.fee!!,
            link = createConcertRequest.link!!,
            region = region,
            regionDetail = createConcertRequest.regionDetail!!,
            group = group,
        )

        val concertObject = concertRepository.save(concert)

        val musicList = musicRepository.findAllByIdIn(createConcertRequest.musicIds)
        val musicMap = musicList.associateBy { it.id }
        val concertSetList = createConcertRequest.musicIds.map { id ->
            val music = musicMap[id] ?: throw MusicIdNotFoundException(id)
            ConcertMusic(concert = concertObject, music = music)
        }
        concertMusicRepository.saveAll(concertSetList)
        return ConcertDto.ConcertDetailResponse(concertObject, concertSetList.sortedBy { it.id })
    }

    fun getConcert(concertId: Long): ConcertDto.ConcertDetailResponse {
        val concert = concertRepository.getConcertByIdFetchJoinConcertMusic(concertId)
            ?: throw ConcertNotFoundException(concertId)

        return ConcertDto.ConcertDetailResponse(concert, concert.playList.sortedBy { it.id })
    }

    fun getConcertDashboard(
        startDate: String?,
        endDate: String?,
        regionParent: String?,
        regionChild: String?,
        composer: String?,
        musicName: String?,
        text: String?,
        currentUser: User?,
        pageable: Pageable,
    ): PageResponse<ConcertDto.ConcertDashboardResponse> {
        val spec = ConcertSpecification()
        startDate?.let { spec.filteredByDateStart(dateFormatter.parse(it)) }
        endDate?.let { spec.filteredByDateEnd(dateFormatter.parse(it)) }
        regionParent?.let { if (regionParent != "전체") spec.filteredByRegionParent(regionParent) }
        regionChild?.let { if (regionChild != "전체") spec.filteredByRegionChild(regionChild) }
        composer?.let { spec.filteredByComposer(composer) }
        musicName?.let { spec.filteredByMusicName(musicName) }
        text?.let { spec.filteredByText(text) }
        val concerts = concertRepository.findAll(spec.build(), pageable)
        return if (currentUser == null) {
            val concertResponses = concerts.map { ConcertDto.ConcertDashboardResponse(it) }
            PageResponse(concertResponses, pageable.pageNumber)
        } else {
            val concertIdSet = concerts.map { it.id }.toSet()
            val friendCountList = concertParticipantRepository.findFriendCount(concertIdSet, currentUser.id)
            val friendCountMap = friendCountList.associate { it.concertId to it.count }
            val concertResponses = concerts.map { ConcertDto.ConcertDashboardResponse(it, friendCountMap[it.id]) }
            PageResponse(concertResponses, pageable.pageNumber)
        }
    }

    fun updateConcertDetail(
        user: User,
        concertId: Long,
        updateConcertDetailRequest: ConcertDto.UpdateConcertDetailRequest,
    ) {
        val concert = concertRepository.findByIdOrNull(concertId)
            ?: throw ConcertNotFoundException(concertId)

        groupMemberAuthorityService.checkMemberAuthorities(GroupMember.MemberRole.ADMIN, concert.group.id, user.id)
        with(concert) {
            title = updateConcertDetailRequest.title!!
            subtitle = updateConcertDetailRequest.subtitle!!.trim()
            conductor = updateConcertDetailRequest.conductor!!
            solist = updateConcertDetailRequest.solist!!
            host = updateConcertDetailRequest.host!!
            support = updateConcertDetailRequest.support!!
            qna = updateConcertDetailRequest.qna!!
            posterImg = updateConcertDetailRequest.posterImg!!
            concertDate = dateTimeFormatter.parse(updateConcertDetailRequest.concertDate!!)
            concertRunningTime = updateConcertDetailRequest.concertRunningTime!!
            region = regionService.getOrCreateRegion(
                updateConcertDetailRequest.regionParent!!,
                updateConcertDetailRequest.regionChild!!,
            )
            regionDetail = updateConcertDetailRequest.regionDetail!!
            fee = updateConcertDetailRequest.fee!!
        }
        concertRepository.save(concert)
    }

    fun updateConcertInfo(user: User, concertId: Long, updateConcertInfoRequest: ConcertDto.UpdateConcertInfoRequest) {
        val concert = concertRepository.findByIdOrNull(concertId)
            ?: throw ConcertNotFoundException(concertId)
        groupMemberAuthorityService.checkMemberAuthorities(GroupMember.MemberRole.ADMIN, concert.group.id, user.id)
        concert.concertInfo = updateConcertInfoRequest.concertInfo!!
        concertRepository.save(concert)
    }

    @Transactional
    fun updateConcertSetList(
        user: User,
        concertId: Long,
        updateConcertSetListRequest: ConcertDto.UpdateConcertSetListRequest,
    ) {
        val concert = concertRepository.findByIdOrNull(concertId)
            ?: throw ConcertNotFoundException(concertId)

        groupMemberAuthorityService.checkMemberAuthorities(
            GroupMember.MemberRole.ADMIN,
            concert.group.id,
            user.id,
        )

        val musicIds = updateConcertSetListRequest.musicIds

        if (musicIds.isEmpty()) {
            concertMusicRepository.deleteAllByConcertId(concertId)
        } else {
            concertMusicRepository.deleteAllByConcertIdAndMusicIdNotIn(concertId, musicIds)
            val musicList = musicRepository.findAllByIdIn(musicIds)
            val musicMap = musicList.associateBy { it.id }
            val existingIdSet = concertMusicRepository.findMusicIdAllByConcertId(concertId)
            val concertSetList = updateConcertSetListRequest.musicIds.filter { musicId ->
                !existingIdSet.contains(musicId)
            }.map { id ->
                val music = musicMap[id] ?: throw MusicIdNotFoundException(id)
                ConcertMusic(concert = concert, music = music)
            }
            concertMusicRepository.saveAll(concertSetList)
        }
    }

    fun getConcertParticipantsList(
        concertId: Long,
    ): ConcertParticipantDto.ConcertParticipantsListResponse {
        if (!concertRepository.existsById(concertId)) {
            throw ConcertNotFoundException(concertId)
        }

        val concertParticipants = concertParticipantRepository.findParticipantsByConcertId(concertId)

        val partNameToParticipants = concertParticipants.groupBy { it.part }
        val concertParticipantByPartList = partNameToParticipants.map { (partName, partParticipants) ->
            val partResponse = ConcertParticipantDto.ConcertParticipantsByPartResponse(partName)
            partParticipants.forEach { concertParticipant ->
                partResponse.add(concertParticipant)
            }
            partResponse
        }
        return ConcertParticipantDto.ConcertParticipantsListResponse(concertParticipantByPartList)
    }
}
