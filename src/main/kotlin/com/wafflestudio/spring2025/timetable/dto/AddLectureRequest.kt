package com.wafflestudio.spring2025.timetable.dto

// 시간표에 강의 추가를 위한 요청 DTO
data class AddLectureRequest(
    // 시간표에 추가할 강의의 ID
    val lectureId: Long,
)
