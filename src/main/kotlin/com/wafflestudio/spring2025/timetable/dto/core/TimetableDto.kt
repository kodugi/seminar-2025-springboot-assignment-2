package com.wafflestudio.spring2025.timetable.dto.core

import com.wafflestudio.spring2025.timetable.model.Timetable

class TimetableDto(
    val id: Long,
    val year: Int,
    val semester: String,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    constructor(timetable: Timetable) : this(
        id = timetable.id!!,
        year = timetable.year,
        semester = timetable.semester,
        name = timetable.name,
        createdAt = timetable.createdAt!!.toEpochMilli(),
        updatedAt = timetable.updatedAt!!.toEpochMilli(),
    )
}