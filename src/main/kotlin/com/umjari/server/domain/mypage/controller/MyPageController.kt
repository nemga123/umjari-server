package com.umjari.server.domain.mypage.controller

import com.umjari.server.domain.mypage.dto.MyPageDto
import com.umjari.server.domain.mypage.service.MyPageService
import com.umjari.server.domain.post.dto.CommunityPostDto
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import com.umjari.server.global.pagination.PageResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "MyPage", description = "my page APIs")
@RestController
@RequestMapping("/api/v1/me")
class MyPageController(
    private val myPageService: MyPageService,
) {
    @GetMapping("/posts/")
    @ResponseStatus(HttpStatus.OK)
    fun getMyPostList(
        @PageableDefault(
            size = 20,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC,
        ) pageable: Pageable,
        @CurrentUser currentUser: User,
    ): PageResponse<MyPageDto.MyPostListResponse> {
        return myPageService.getMyPostList(pageable, currentUser)
    }

    @GetMapping("/liked-posts/")
    @ResponseStatus(HttpStatus.OK)
    fun getLikedPostList(
        @PageableDefault(
            size = 20,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC,
        ) pageable: Pageable,
        @CurrentUser currentUser: User,
    ): PageResponse<CommunityPostDto.PostSimpleResponse> {
        return myPageService.getLikedPostList(pageable, currentUser)
    }
}
