package com.wafflestudio.spring2025.timetable.repository

import com.wafflestudio.spring2025.timetable.model.Timetable
import org.springframework.data.repository.ListCrudRepository

interface TimetableRepository : ListCrudRepository<Timetable, Long> {
    // 특정 사용자의 모든 시간표 목록을 조회
    fun findAllByUserId(userId: Long): List<Timetable>
}
