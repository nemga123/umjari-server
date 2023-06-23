package com.umjari.server.domain.user.service

import com.umjari.server.domain.concert.dto.ConcertParticipantDto
import com.umjari.server.domain.concert.repository.ConcertParticipantRepository
import com.umjari.server.domain.friend.repository.FriendRepository
import com.umjari.server.domain.group.dto.GroupDto
import com.umjari.server.domain.group.repository.GroupMemberRepository
import com.umjari.server.domain.image.service.ImageService
import com.umjari.server.domain.user.dto.UserDto
import com.umjari.server.domain.user.exception.DuplicatedUserNicknameException
import com.umjari.server.domain.user.exception.DuplicatedUserProfileNameException
import com.umjari.server.domain.user.exception.UserProfileNameNotFoundException
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val concertParticipantRepository: ConcertParticipantRepository,
    private val imageService: ImageService,
    private val friendRepository: FriendRepository,
) {
    fun checkDuplicatedNickname(nicknameRequest: UserDto.NicknameRequest) {
        if (userRepository.existsByNickname(nicknameRequest.nickname!!)) {
            throw DuplicatedUserNicknameException(nicknameRequest.nickname)
        }
    }
    fun getJoinedGroupList(user: User): UserDto.UserGroupListResponse {
        val groupList = groupMemberRepository.findGroupListByUserId(user.id)
        val career = groupList.map { GroupDto.GroupUserResponse(it) }
        return UserDto.UserGroupListResponse(career = career)
    }

    fun getJoinedConcertList(profileName: String): ConcertParticipantDto.ParticipatedConcertListResponse {
        val user = userRepository.findByProfileName(profileName)
            ?: throw UserProfileNameNotFoundException(profileName)

        val concertList = concertParticipantRepository.findConcertListByJoinedUserId(user.id)
        val response = concertList.map { ConcertParticipantDto.ParticipatedConcertResponse(it) }
        return ConcertParticipantDto.ParticipatedConcertListResponse(response)
    }

    fun getJoinedConcertListGroupByConcertId(
        profileName: String,
    ): ConcertParticipantDto.ParticipatedConcertsGroupByConcertIdListResponse {
        val user = userRepository.findByProfileName(profileName)
            ?: throw UserProfileNameNotFoundException(profileName)

        val concertList = concertParticipantRepository.findConcertListByJoinedUserIdWithPoster(user.id)
        val listGroupByConcertId = concertList.groupBy { it.id }
        return ConcertParticipantDto.ParticipatedConcertsGroupByConcertIdListResponse(listGroupByConcertId)
    }

    fun updateProfileImage(user: User, imageRequest: UserDto.ProfileImageRequest) {
        val originImageUrl = user.profileImage
        if (originImageUrl == imageRequest.image) return
        user.profileImage = imageRequest.image!!
        userRepository.save(user)
        if (originImageUrl.startsWith("http")) {
            imageService.removeImageByUrl(originImageUrl)
        }
    }

    fun updateUserInformation(user: User, updateUserInfo: UserDto.UpdateUserInfoRequest) {
        if (userRepository.existsByNicknameAndIdNot(updateUserInfo.nickname!!, user.id)) {
            throw DuplicatedUserNicknameException(updateUserInfo.nickname)
        }

        if (userRepository.existsByProfileNameAndIdNot(updateUserInfo.profileName!!, user.id)) {
            throw DuplicatedUserProfileNameException(updateUserInfo.profileName)
        }

        with(user) {
            profileName = updateUserInfo.profileName
            nickname = updateUserInfo.nickname
            intro = updateUserInfo.intro
        }

        userRepository.save(user)
    }

    fun getUserInformation(profileName: String, currentUser: User?): UserDto.DetailUserInfoResponse {
        val user = userRepository.findByProfileName(profileName)
            ?: throw UserProfileNameNotFoundException(profileName)

        val isFriend = if (currentUser != null) {
            friendRepository.isFriend(currentUser.id, user.id)
        } else {
            false
        }

        return UserDto.DetailUserInfoResponse(user = user, currentUser = currentUser, isFriend)
    }

    fun getUserIdToUserMapInUserIds(userIds: List<String>): Pair<Set<String>, Map<String, User>> {
        val existingUsers = userRepository.findUserIdsByUserIdIn(userIds)
        val userMap = existingUsers.associateBy { it.userId }
        val existingUserIds = existingUsers.map { it.userId }.toSet()
        return Pair(existingUserIds, userMap)
    }
}
