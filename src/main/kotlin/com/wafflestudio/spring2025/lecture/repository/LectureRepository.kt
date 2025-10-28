package com.wafflestudio.spring2025.lecture.repository

import com.wafflestudio.spring2025.lecture.model.Lecture
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.ListCrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param

interface LectureRepository : ListCrudRepository<Lecture, Long>, PagingAndSortingRepository<Lecture, Long> {

    /**
     * 특정 연도/학기에 개설된 강의를 강의명 또는 교수명 키워드로 검색한다.
     * @param year 연도
     * @param semester 학기
     * @param keyword 검색 키워드
     * @param pageable 페이지네이션 정보 (페이지 번호, 페이지 크기, 정렬 등)
     * @return Page<Lecture> 페이지네이션 결과 객체
     */
    @Query("""
        SELECT * FROM lectures
        WHERE year = :year AND semester = :semester
          AND (course_title LIKE CONCAT('%', :keyword, '%') OR instructor LIKE CONCAT('%', :keyword, '%'))
    """)
    fun searchByYearAndSemesterAndKeyword(
        @Param("year") year: Int,
        @Param("semester") semester: String,
        @Param("keyword") keyword: String,
        pageable: Pageable
    ): Page<Lecture> // Page 타입으로 반환

    /**
     * 특정 연도/학기에 개설된 모든 강의 목록을 조회한다.
     * @param year 연도
     * @param semester 학기
     * @return List<Lecture> 해당 학기 강의 목록
     */
    fun findByYearAndSemester(year: Int, semester: String): List<Lecture>
}