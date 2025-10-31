package com.wafflestudio.spring2025.timetable.dto

// 시간표 이름 수정을 위한 요청 DTO (이름 변경)
data class UpdateTimetableNameRequest(
    // 시간표의 새로운 이름
    val name: String,
)