package com.umjari.server.domain.user.service

import com.umjari.server.domain.user.dto.UserDto
import com.umjari.server.domain.user.exception.DuplicatedUserNicknameException
import com.umjari.server.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun checkDuplicatedNickname(nicknameRequest: UserDto.NicknameRequest) {
        if (userRepository.existsByNickname(nicknameRequest.nickname!!)) {
            throw DuplicatedUserNicknameException(nicknameRequest.nickname!!)
        }
    }
}
