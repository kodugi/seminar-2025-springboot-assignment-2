package com.wafflestudio.spring2025.lecture.dto

import com.wafflestudio.spring2025.lecture.dto.core.LectureDto
import org.springframework.data.domain.Page

// 강의 검색 결과 및 페이지네이션 정보를 담는 응답 DTO
data class LectureSearchResponse(
    // 현재 페이지의 강의 목록
    val lectures: List<LectureDto>,
    // 페이지네이션 관련 정보
    val totalCount: Long,   // 전체 검색 결과 개수
    val totalPages: Int,    // 전체 페이지 수
    val currentPage: Int,   // 현재 페이지 번호 (0부터 시작)
    val hasNext: Boolean    // 다음 페이지 존재 여부
) {
    constructor(lecturePage: Page<LectureDto>) : this(
        lectures = lecturePage.content,          // 현재 페이지 내용
        totalCount = lecturePage.totalElements,  // 전체 개수
        totalPages = lecturePage.totalPages,     // 전체 페이지 수
        currentPage = lecturePage.number,        // 현재 페이지 번호
        hasNext = lecturePage.hasNext()          // 다음 페이지 유무
    )
}