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
    private val scheduleRepository: LectureScheduleRepository,
) {
    fun search(
        year: Int,
        semester: String,
        keyword: String,
        pageable: Pageable,
    ): LectureSearchResponse {
        val lectures =
            lectureRepository.findLecturesByKeyword(
                year = year,
                semester = semester,
                keyword = keyword,
                limit = pageable.pageSize,
                offset = pageable.offset,
            )

        val totalCount =
            lectureRepository.countLecturesByKeyword(
                year = year,
                semester = semester,
                keyword = keyword,
            )

        val lecturePage = PageImpl(lectures, pageable, totalCount)

        val lectureDtos =
            lecturePage.map {
                val schedules = scheduleRepository.findAllByLectureId(it.id!!)
                LectureDto(it, schedules)
            }
        return LectureSearchResponse(lectureDtos)
    }
}
