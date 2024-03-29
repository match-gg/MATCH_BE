package gg.match.controller.common.dto

import org.springframework.data.domain.Page

class PageResult<T>(
    data: Page<T>
) {
    val currentPage: Int
    val totalPage: Int
    val pageSize: Int
    val totalElements: Long
    val content: List<T>

    init{
        currentPage = data.pageable.pageNumber
        pageSize = data.pageable.pageSize
        totalPage = data.totalPages
        totalElements = data.totalElements
        content = data.content
    }

    companion object{
        fun <T> ok(data: Page<T>): PageResult<T> {
            return PageResult(data)
        }
    }
}