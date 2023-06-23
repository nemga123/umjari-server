package com.umjari.server.domain.guestbook.service

import com.umjari.server.domain.friend.repository.FriendRepository
import com.umjari.server.domain.guestbook.dto.GuestBookDto
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

@Service
class GuestBookService(
    private val guestBookRepository: GuestBookRepository,
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository,
) {
    fun postGuestBook(targetUserId: Long, postGuestBookRequest: GuestBookDto.PostGuestBookRequest, currentUser: User) {
        val targetUser = userRepository.findByIdOrNull(targetUserId)
            ?: throw UserIdNotFoundException(targetUserId)

        if (postGuestBookRequest.private && !friendRepository.isFriend(targetUserId, currentUser.id)) {
            throw PrivateGuestBookNotAuthorizedException()
        }

        val obj = GuestBook(
            user = targetUser,
            author = currentUser,
            content = postGuestBookRequest.content!!,
            private = postGuestBookRequest.private,
        )
        guestBookRepository.save(obj)
    }

    fun listGuestBook(
        profileName: String,
        currentUser: User?,
        pageable: Pageable,
    ): PageResponse<GuestBookDto.GuestBookResponse> {
        val targetUser = userRepository.findByProfileName(profileName)
            ?: throw UserProfileNameNotFoundException(profileName)

        lateinit var guestBooks: Page<GuestBook>
        if (currentUser != null) {
            guestBooks = guestBookRepository.findAllByUserIdWithAuthor(targetUser.id, currentUser.id, pageable)
        } else {
            guestBooks = guestBookRepository.findAllByUserId(targetUser.id, pageable)
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
