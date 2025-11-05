package com.wafflestudio.spring2025.timetable.dto

data class CreateTimetableRequest(
    val year: Int,
    val semester: String,
    val name: String,
)
