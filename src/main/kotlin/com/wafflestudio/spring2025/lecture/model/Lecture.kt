package com.wafflestudio.spring2025.lecture.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(value = "lectures")
class Lecture(
    @Id
    var id: Long? = null,
    var year: Int, // 강의 개설 연도
    var semester: String, // 강의 개설 학기 (예: 봄학기)
    var courseNumber: String, // 교과목 번호 (예: F21.201)
    var lectureNumber: String, // 강좌 번호 (예: 001)
    var courseTitle: String, // 교과목명 (예: 대학영어2)
    var courseSubtitle: String?, // 부제명 (예: 글쓰기)
    var credit: Int, // 학점 수
    var instructor: String, // 담당교수 이름
    var category: String?, // 교과 구분 (예: "전공선택")
    var college: String?, // 개설 대학 (예: "공과대학")
    var department: String?, // 개설 학과 (예: "컴퓨터공학부")
    var academicCourse: String?, // 이수 과정 (예: "학사")
    var academicYear: String?, // 학년 (예: "1학년")
    @CreatedDate
    var createdAt: Instant? = null,
    @LastModifiedDate
    var updatedAt: Instant? = null,
)
