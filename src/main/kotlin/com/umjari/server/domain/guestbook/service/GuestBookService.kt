package com.umjari.server.domain.guestbook.service

import com.umjari.server.domain.friend.repository.FriendRepository
import com.umjari.server.domain.guestbook.dto.GuestBookDto
import com.umjari.server.domain.guestbook.exception.CannotPostToOwnGuestbookException
import com.umjari.server.domain.guestbook.exception.GuestBookAlreadyPostedOnToday
import com.umjari.server.domain.guestbook.exception.GuestBookIdNotFoundException
import com.umjari.server.domain.guestbook.exception.PrivateGuestBookNotAuthorizedException
import com.umjari.server.domain.guestbook.model.GuestBook
import com.umjari.server.domain.guestbook.repository.GuestBookRepository
import com.umjari.server.domain.user.exception.UserIdNotFoundException
import com.umjari.server.domain.user.exception.UserProfileNameNotFoundException
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GuestBookService(
    private val guestBookRepository: GuestBookRepository,
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository,
) {
    fun postGuestBook(targetUserId: Long, postGuestBookRequest: GuestBookDto.PostGuestBookRequest, currentUser: User) {
        if (currentUser.id == targetUserId) {
            throw CannotPostToOwnGuestbookException()
        }

        val targetUser = userRepository.findByIdOrNull(targetUserId)
            ?: throw UserIdNotFoundException(targetUserId)
        if (guestBookRepository.existsByUserIdAndAuthorIdAndCreatedAtAfter(
                targetUserId,
                currentUser.id,
                LocalDate.now().atStartOfDay(),
            )
        ) {
            throw GuestBookAlreadyPostedOnToday()
        }

        if (postGuestBookRequest.private && !friendRepository.isFriend(targetUserId, currentUser.id)) {
            throw PrivateGuestBookNotAuthorizedException()
        }

        GuestBook(
            user = targetUser,
            author = currentUser,
            content = postGuestBookRequest.content!!,
            private = postGuestBookRequest.private,
        ).also { guestBook -> guestBookRepository.save(guestBook) }
    }

    fun listGuestBook(
        profileName: String,
        currentUser: User?,
        pageable: Pageable,
    ): PageResponse<GuestBookDto.GuestBookResponse> {
        val targetUser = userRepository.findByProfileName(profileName)
            ?: throw UserProfileNameNotFoundException(profileName)

        val guestBooks: Page<GuestBook> = if (currentUser == null) {
            guestBookRepository.findAllOpenGuestBookByUserId(targetUser.id, pageable)
        } else if (currentUser.id == targetUser.id) {
            guestBookRepository.findAllByUserId(targetUser.id, pageable)
        } else {
            guestBookRepository.findAllByUserIdWithAuthor(targetUser.id, currentUser.id, pageable)
        }

        val guestBookPage = guestBooks.map { GuestBookDto.GuestBookResponse(it, currentUser) }

        return PageResponse(guestBookPage, pageable.pageNumber)
    }

    fun updateGuestBookPost(
        guestBookId: Long,
        updateGuestBookRequest: GuestBookDto.PostGuestBookRequest,
        currentUser: User,
    ) {
        val guestBook = guestBookRepository.findByIdAndAuthorId(guestBookId, currentUser.id)
            ?: throw GuestBookIdNotFoundException(guestBookId)

        if (updateGuestBookRequest.private && !friendRepository.isFriend(guestBook.user.id, currentUser.id)) {
            throw PrivateGuestBookNotAuthorizedException()
        }

        with(guestBook) {
            content = updateGuestBookRequest.content!!
            private = updateGuestBookRequest.private
        }
        guestBookRepository.save(guestBook)
    }

    fun deleteGuestBookPost(guestBookId: Long, currentUser: User) {
        val guestBook = guestBookRepository.findByIdOrNull(guestBookId)
            ?: throw GuestBookIdNotFoundException(guestBookId)

        if (guestBook.user.id != currentUser.id && guestBook.author.id != currentUser.id) {
            throw GuestBookIdNotFoundException(guestBookId)
        }

        guestBookRepository.delete(guestBook)
    }
}
