package com.wafflestudio.spring2025.timetable.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(value = "timetables")
class Timetable (
    @Id var id: Long? = null,
    var userId: Long,                   // 시간표 소유자 (users 테이블 참조)
    var year: Int,                      // 시간표 대상 연도 (예: 2025)
    var semester: String,               // 시간표 대상 학기 (예: 봄학기)
    var name: String,                   // 사용자가 지정한 시간표 이름
    @CreatedDate
    var createdAt: Instant? = null,
    @LastModifiedDate
    var updatedAt: Instant? = null,
)
