package com.wafflestudio.spring2025.lecture.repository

import com.wafflestudio.spring2025.lecture.model.Lecture
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.ListCrudRepository
import org.springframework.data.repository.query.Param

interface LectureRepository : ListCrudRepository<Lecture, Long> {

    /**
     * (수정됨)
     * 특정 연도/학기 강의를 키워드로 검색 (데이터 목록 조회 - 수동 페이징)
     * LIMIT, OFFSET을 직접 파라미터로 받습니다.
     */
    @Query("""
        SELECT * FROM lectures
        WHERE year = :year AND semester = :semester
          AND (course_title LIKE CONCAT('%', :keyword, '%') OR instructor LIKE CONCAT('%', :keyword, '%'))
        ORDER BY course_title, lecture_number -- 페이징을 위해 정렬 순서 보장
        LIMIT :limit OFFSET :offset
    """)
    fun findLecturesByKeyword(
        @Param("year") year: Int,
        @Param("semester") semester: String,
        @Param("keyword") keyword: String,
        @Param("limit") limit: Int,
        @Param("offset") offset: Long
    ): List<Lecture> // Page 대신 List 반환

    /**
     * (추가됨)
     * 특정 연도/학기 강의를 키워드로 검색 (전체 개수 조회)
     */
    @Query("""
        SELECT COUNT(*) FROM lectures
        WHERE year = :year AND semester = :semester
          AND (course_title LIKE CONCAT('%', :keyword, '%') OR instructor LIKE CONCAT('%', :keyword, '%'))
    """)
    fun countLecturesByKeyword(
        @Param("year") year: Int,
        @Param("semester") semester: String,
        @Param("keyword") keyword: String
    ): Long // COUNT 결과이므로 Long 반환

    /**
     * 특정 연도/학기에 개설된 모든 강의 목록을 조회한다.
     * @param year 연도
     * @param semester 학기
     * @return List<Lecture> 해당 학기 강의 목록
     */
    fun findByYearAndSemester(year: Int, semester: String): List<Lecture>
}