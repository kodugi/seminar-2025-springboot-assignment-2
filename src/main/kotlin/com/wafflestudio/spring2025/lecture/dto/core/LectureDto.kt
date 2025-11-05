package com.wafflestudio.spring2025.lecture.dto.core

import com.wafflestudio.spring2025.lecture.model.Lecture
import com.wafflestudio.spring2025.lecture.model.LectureSchedule

data class LectureDto(
    val id: Long,
    val year: Int,
    val semester: String,
    val courseNumber: String,
    val lectureNumber: String,
    val courseTitle: String,
    val courseSubtitle: String?,
    val credit: Int,
    val instructor: String,
    val category: String?,
    val college: String?,
    val department: String?,
    val academicCourse: String?,
    val academicYear: String?,
    val schedules: List<LectureScheduleDto>,
) {
    constructor(lecture: Lecture, schedules: List<LectureSchedule>) : this(
        id = lecture.id!!,
        year = lecture.year,
        semester = lecture.semester,
        courseNumber = lecture.courseNumber,
        lectureNumber = lecture.lectureNumber,
        courseTitle = lecture.courseTitle,
        courseSubtitle = lecture.courseSubtitle,
        credit = lecture.credit,
        instructor = lecture.instructor,
        category = lecture.category,
        college = lecture.college,
        department = lecture.department,
        academicCourse = lecture.academicCourse,
        academicYear = lecture.academicYear,
        schedules = schedules.map { LectureScheduleDto(it) },
    )
}

data class LectureScheduleDto(
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val place: String?,
) {
    constructor(schedule: LectureSchedule) : this(
        dayOfWeek = schedule.dayOfWeek,
        startTime = schedule.startTime.toString(),
        endTime = schedule.endTime.toString(),
        place = schedule.place,
    )
}
