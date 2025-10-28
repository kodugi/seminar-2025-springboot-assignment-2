package com.wafflestudio.spring2025.lecture.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalTime

@Table("lecture_schedules")
class LectureSchedule (
    @Id
    var id: Long? = null,
    var lectureId: Long,            // 연결된 lectures 테이블의 ID (외래 키)
    var dayOfWeek: Int,             // 요일 (0: 월요일, 1: 화요일 ... 5: 금요일)
    var startTime: LocalTime,       // 수업 시작 시간
    var endTime: LocalTime,         // 수업 종료 시간
    var place: String?,             // 강의실
    @CreatedDate
    var createdAt: Instant? = null,
    @LastModifiedDate
    var updatedAt: Instant? = null,
)