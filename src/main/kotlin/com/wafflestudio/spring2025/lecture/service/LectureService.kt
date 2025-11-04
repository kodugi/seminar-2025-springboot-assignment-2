package com.wafflestudio.spring2025.lecture.service

import com.wafflestudio.spring2025.lecture.dto.LectureSearchResponse
import com.wafflestudio.spring2025.lecture.dto.core.LectureDto
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.lecture.repository.LectureScheduleRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
    private val scheduleRepository: LectureScheduleRepository
) {
    fun search(
        year: Int,
        semester: String,
        keyword: String,
        pageable: Pageable
    ): LectureSearchResponse {

        // (수정됨)
        // 1. 데이터 목록 조회 (수동 페이징 파라미터 전달)
        val lectures = lectureRepository.findLecturesByKeyword(
            year = year,
            semester = semester,
            keyword = keyword,
            limit = pageable.pageSize,
            offset = pageable.offset
        )

        // 2. 전체 개수 조회
        val totalCount = lectureRepository.countLecturesByKeyword(
            year = year,
            semester = semester,
            keyword = keyword
        )

        // 3. PageImpl을 사용해 Page 객체 수동 생성
        val lecturePage = PageImpl(lectures, pageable, totalCount)

        // (기존 코드와 동일)
        val lectureDtos = lecturePage.map{
            val schedules = scheduleRepository.findAllByLectureId(it.id!!)
            LectureDto(it, schedules)
        }
        return LectureSearchResponse(lectureDtos)
    }
}