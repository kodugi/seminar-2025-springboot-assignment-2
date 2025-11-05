package com.wafflestudio.spring2025.timetable.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(value = "timetable_lectures")
class TimetableLecture(
    @Id
    var id: Long? = null,
    var timetableId: Long,
    var lectureId: Long,
    @CreatedDate
    var createdAt: Instant? = null,
    @LastModifiedDate
    var updatedAt: Instant? = null,
)
