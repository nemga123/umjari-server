package com.umjari.server.global.pagination

import org.springframework.data.domain.Page

data class PageResponse<T>(
    val contents: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val currentPage: Int,
) {
    constructor(page: Page<T>, currentPage: Int) : this(
        contents = page.content,
        totalPages = page.totalPages,
        totalElements = page.totalElements,
        currentPage = currentPage,
    )
}
