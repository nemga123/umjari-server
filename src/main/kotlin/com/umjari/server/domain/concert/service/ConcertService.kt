package com.umjari.server.domain.concert.service

import com.umjari.server.domain.concert.dto.ConcertDto
import com.umjari.server.domain.concert.exception.ConcertNotFoundException
import com.umjari.server.domain.concert.model.Concert
import com.umjari.server.domain.concert.repository.ConcertRepository
import com.umjari.server.domain.concert.specification.ConcertSpecification
import com.umjari.server.domain.group.exception.GroupIdNotFoundException
import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.group.service.GroupMemberAuthorityService
import com.umjari.server.domain.region.service.RegionService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class ConcertService(
    private val concertRepository: ConcertRepository,
    private val regionService: RegionService,
    private val groupRepository: GroupRepository,
    private val groupMemberAuthorityService: GroupMemberAuthorityService,
) {
    private final val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private final val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

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
            host = createConcertRequest.host!!,
            support = createConcertRequest.support!!,
            qna = createConcertRequest.qna!!,
            concertInfo = createConcertRequest.concertInfo!!,
            posterImg = createConcertRequest.posterImg!!,
            concertDate = dateTimeFormatter.parse(createConcertRequest.concertDate!!),
            concertRunningTime = createConcertRequest.concertRunningTime!!,
            fee = createConcertRequest.fee!!,
            region = region,
            regionDetail = createConcertRequest.regionDetail!!,
            group = group,
        )

        concertRepository.save(concert)

        return ConcertDto.ConcertDetailResponse(concert)
    }

    fun getConcert(concertId: Long): ConcertDto.ConcertDetailResponse {
        val concert = concertRepository.findByIdOrNull(concertId)
            ?: throw ConcertNotFoundException(concertId)
        return ConcertDto.ConcertDetailResponse(concert)
    }

    fun getConcertDashboard(
        startDate: String?,
        endDate: String?,
        regionParent: String?,
        regionChild: String?,
        text: String?,
        pageable: Pageable,
    ): PageResponse<ConcertDto.ConcertSimpleResponse> {
        val spec = ConcertSpecification()
        startDate?.let { spec.filteredByDateStart(dateFormatter.parse(it)) }
        endDate?.let { spec.filteredByDateEnd(dateFormatter.parse(it)) }
        regionParent?.let { spec.filteredByRegionParent(regionParent) }
        regionChild?.let { spec.filteredByRegionChild(regionChild) }
        text?.let { spec.filteredByText(text) }
        val concerts = concertRepository.findAll(spec.build(), pageable)
        val concertResponses = concerts.map { ConcertDto.ConcertSimpleResponse(it) }
        return PageResponse(concertResponses, pageable.pageNumber)
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
}
