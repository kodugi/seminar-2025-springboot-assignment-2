package com.wafflestudio.spring2025.lecture.repository

import com.wafflestudio.spring2025.lecture.model.LectureSchedule
import org.springframework.data.repository.ListCrudRepository

interface LectureScheduleRepository : ListCrudRepository<LectureSchedule, Long> {
    fun findAllByLectureId(lectureId: Long): List<LectureSchedule>

    fun deleteAllByLectureIdIn(lectureIds: List<Long>)
}
