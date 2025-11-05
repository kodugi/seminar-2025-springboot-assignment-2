package com.wafflestudio.spring2025.lecture.dto

import com.wafflestudio.spring2025.lecture.dto.core.LectureDto
import org.springframework.data.domain.Page

data class LectureSearchResponse(
    val lectures: List<LectureDto>,
    val totalCount: Long,
    val totalPages: Int,
    val currentPage: Int,
    val hasNext: Boolean,
) {
    constructor(lecturePage: Page<LectureDto>) : this(
        lectures = lecturePage.content,
        totalCount = lecturePage.totalElements,
        totalPages = lecturePage.totalPages,
        currentPage = lecturePage.number,
        hasNext = lecturePage.hasNext(),
    )
}
