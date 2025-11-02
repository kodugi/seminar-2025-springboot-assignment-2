package com.wafflestudio.spring2025.lecture.service

import com.wafflestudio.spring2025.lecture.dto.LectureSearchResponse
import com.wafflestudio.spring2025.lecture.dto.core.LectureDto
import com.wafflestudio.spring2025.lecture.model.Lecture
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.lecture.repository.LectureScheduleRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

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
        val lecturePage = lectureRepository.searchByYearAndSemesterAndKeyword(year, semester, keyword, pageable)
        val lectureDtos = lecturePage.map{
            val schedules = scheduleRepository.findAllByLectureId(it.id!!)
            LectureDto(it, schedules)
        }
        return LectureSearchResponse(lectureDtos)
    }
}