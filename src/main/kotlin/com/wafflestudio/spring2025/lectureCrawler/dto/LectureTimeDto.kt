package com.wafflestudio.spring2025.lectureCrawler.dto

import java.time.LocalTime

data class LectureTimeDto(
    val dayOfWeek: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
)
