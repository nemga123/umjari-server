package com.umjari.server.domain.concert.service

import com.umjari.server.domain.concert.dto.ConcertDto
import com.umjari.server.domain.concert.model.Concert
import com.umjari.server.domain.concert.repository.ConcertRepository
import com.umjari.server.domain.group.exception.GroupIdNotFoundException
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.region.model.Region
import com.umjari.server.domain.region.repository.RegionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ConcertService(
    private val concertRepository: ConcertRepository,
    private val regionRepository: RegionRepository,
    private val groupRepository: GroupRepository,
) {
    fun createConcert(
        createConcertRequest: ConcertDto.CreateConcertRequest,
        groupId: Long,
    ): ConcertDto.ConcertDetailResponse {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)

        val region = regionRepository.findByParentAndChild(
            createConcertRequest.regionParent!!,
            createConcertRequest.regionChild!!,
        ) ?: run {
            val obj = Region(createConcertRequest.regionParent, createConcertRequest.regionChild)
            regionRepository.save(obj)
        }

        val concert = Concert(
            title = createConcertRequest.title!!,
            subtitle = createConcertRequest.subtitle!!,
            conductor = createConcertRequest.conductor!!,
            host = createConcertRequest.host!!,
            support = createConcertRequest.support!!,
            qna = createConcertRequest.qna!!,
            concertInfo = createConcertRequest.concertInfo!!,
            posterImg = createConcertRequest.posterImg!!,
            concertDate = createConcertRequest.concertDate!!,
            concertRunningTime = createConcertRequest.concertRunningTime!!,
            fee = createConcertRequest.fee!!,
            region = region,
            regionDetail = createConcertRequest.regionDetail!!,
            group = group,
        )

        return ConcertDto.ConcertDetailResponse(concert)
    }
}
