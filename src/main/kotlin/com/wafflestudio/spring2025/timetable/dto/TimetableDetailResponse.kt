package com.wafflestudio.spring2025.timetable.dto

import com.wafflestudio.spring2025.lecture.dto.core.LectureDto

// 시간표 상세 조회 시 사용될 응답 DTO
data class TimetableDetailResponse(
    // 시간표 기본 정보
    val id: Long,
    val year: Int,
    val semester: String,
    val name: String,
    // 시간표에 포함된 강의 상세 정보 목록
    val lectures: List<LectureDto>,
    // 시간표에 포함된 강의들의 총 학점
    val totalCredit: Int
)