package com.umjari.server.domain.concert.service

import com.umjari.server.domain.concert.dto.ConcertDto
import com.umjari.server.domain.concert.exception.ConcertNotFoundException
import com.umjari.server.domain.concert.model.Concert
import com.umjari.server.domain.concert.repository.ConcertRepository
import com.umjari.server.domain.group.exception.GroupIdNotFoundException
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.region.service.RegionService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class ConcertService(
    private val concertRepository: ConcertRepository,
    private val regionService: RegionService,
    private val groupRepository: GroupRepository,
) {
    private final val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun createConcert(
        createConcertRequest: ConcertDto.CreateConcertRequest,
        groupId: Long,
    ): ConcertDto.ConcertDetailResponse {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)

        val region = regionService.getOrCreateRegion(
            createConcertRequest.regionParent!!,
            createConcertRequest.regionChild!!,
        )

        val concert = Concert(
            title = createConcertRequest.title!!,
            subtitle = createConcertRequest.subtitle!!,
            conductor = createConcertRequest.conductor!!,
            host = createConcertRequest.host!!,
            support = createConcertRequest.support!!,
            qna = createConcertRequest.qna!!,
            concertInfo = createConcertRequest.concertInfo!!,
            posterImg = createConcertRequest.posterImg!!,
            concertDate = dateFormatter.parse(createConcertRequest.concertDate!!),
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

    fun updateConcertDetail(concertId: Long, updateConcertDetailRequest: ConcertDto.UpdateConcertDetailRequest) {
        val concert = concertRepository.findByIdOrNull(concertId)
            ?: throw ConcertNotFoundException(concertId)
        with(concert) {
            title = updateConcertDetailRequest.title!!
            subtitle = updateConcertDetailRequest.subtitle!!
            conductor = updateConcertDetailRequest.conductor!!
            host = updateConcertDetailRequest.host!!
            support = updateConcertDetailRequest.support!!
            qna = updateConcertDetailRequest.qna!!
            posterImg = updateConcertDetailRequest.posterImg!!
            concertDate = dateFormatter.parse(updateConcertDetailRequest.concertDate!!)
            concertRunningTime = updateConcertDetailRequest.concertRunningTime!!
            fee = updateConcertDetailRequest.fee!!
        }
        concertRepository.save(concert)
    }

    private fun updateRegionOfConcert(concert: Concert, regionParent: String, regionChild: String) {
        if (concert.region.parent != regionParent && concert.region.child != regionChild) {
            val region = regionService.getOrCreateRegion(regionParent, regionChild)
            concert.region = region
        }
    }

    fun updateConcertInfo(concertId: Long, updateConcertInfoRequest: ConcertDto.UpdateConcertInfoRequest) {
        val concert = concertRepository.findByIdOrNull(concertId)
            ?: throw ConcertNotFoundException(concertId)
        concert.concertInfo = updateConcertInfoRequest.concertInfo!!
        concertRepository.save(concert)
    }
}
