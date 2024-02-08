package com.umjari.server.domain.concert.service

import com.umjari.server.domain.concert.dto.ConcertCommentDto
import com.umjari.server.domain.concert.exception.ConcertCommentIdNotFoundException
import com.umjari.server.domain.concert.exception.ConcertNotFoundException
import com.umjari.server.domain.concert.exception.DuplicatedUserConcertComment
import com.umjari.server.domain.concert.model.ConcertComment
import com.umjari.server.domain.concert.repository.ConcertCommentRepository
import com.umjari.server.domain.concert.repository.ConcertRepository
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ConcertCommentService(
    private val concertCommentRepository: ConcertCommentRepository,
    private val concertRepository: ConcertRepository,
) {
    fun createConcertComment(
        user: User,
        createConcertCommentRequest: ConcertCommentDto.CreateConcertCommentRequest,
        concertId: Long,
    ): ConcertCommentDto.ConcertCommentResponse {
        if (concertCommentRepository.existsConcertCommentByUserIdAndConcertId(user.id, concertId)) {
            throw DuplicatedUserConcertComment()
        }

        val concertComment = ConcertComment(
            user = user,
            concert = concertRepository.findByIdOrNull(concertId) ?: throw ConcertNotFoundException(concertId),
            comment = createConcertCommentRequest.comment,
        )

        val concertCommentObject = concertCommentRepository.save(concertComment)
        return ConcertCommentDto.ConcertCommentResponse(concertCommentObject, true)
    }

    fun updateConcertComment(
        user: User,
        commentRequest: ConcertCommentDto.CreateConcertCommentRequest,
        concertId: Long,
        commentId: Long,
    ): ConcertCommentDto.ConcertCommentResponse {
        val concertComment = concertCommentRepository.findConcertCommentByIdAndUserIdAndConcertId(
            commentId,
            user.id,
            concertId,
        )
            ?: throw ConcertCommentIdNotFoundException(commentId)

        concertComment.comment = commentRequest.comment
        val concertCommentObject = concertCommentRepository.save(concertComment)
        return ConcertCommentDto.ConcertCommentResponse(concertCommentObject, true)
    }

    fun deleteConcertComment(
        user: User,
        concertId: Long,
        commentId: Long,
    ) {
        val deletedRow = concertCommentRepository.deleteConcertCommentByIdAndUserIdAndConcertId(
            concertId,
            user.id,
            concertId,
        )
        if (deletedRow == 0L) {
            throw ConcertCommentIdNotFoundException(commentId)
        }
    }

    fun getConcertCommentList(
        user: User,
        concertId: Long,
        pageable: Pageable,
    ): PageResponse<ConcertCommentDto.ConcertCommentResponse> {
        val concert = concertRepository.findByIdOrNull(concertId)
            ?: throw ConcertNotFoundException(concertId)

        val commentList = concertCommentRepository.getAllByConcertId(concert.id, pageable)
        val pagedResponse = commentList.map { ConcertCommentDto.ConcertCommentResponse(it, it.user.id == user.id) }
        return PageResponse(pagedResponse, pageable.pageNumber)
    }
}
