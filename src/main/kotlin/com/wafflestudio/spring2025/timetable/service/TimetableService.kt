package com.wafflestudio.spring2025.timetable.service

import com.wafflestudio.spring2025.lecture.LectureNotFoundException
import com.wafflestudio.spring2025.lecture.dto.core.LectureDto
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.lecture.repository.LectureScheduleRepository
import com.wafflestudio.spring2025.timetable.LectureOverlapException
import com.wafflestudio.spring2025.timetable.TimetableAccessForbiddenException
import com.wafflestudio.spring2025.timetable.TimetableBlankNameException
import com.wafflestudio.spring2025.timetable.TimetableDeleteForbiddenException
import com.wafflestudio.spring2025.timetable.TimetableNotFoundException
import com.wafflestudio.spring2025.timetable.TimetableUpdateForbiddenException
import com.wafflestudio.spring2025.timetable.dto.CreateTimetableResponse
import com.wafflestudio.spring2025.timetable.dto.GetTimetableResponse
import com.wafflestudio.spring2025.timetable.dto.TimetableDetailResponse
import com.wafflestudio.spring2025.timetable.dto.core.TimetableDto
import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.timetable.model.TimetableLecture
import com.wafflestudio.spring2025.timetable.repository.TimetableLectureRepository
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository
import org.springframework.stereotype.Service

@Service
class TimetableService(
    private val timetableRepository: TimetableRepository,
    private val timetableLectureRepository: TimetableLectureRepository,
    private val lectureRepository: LectureRepository,
    private val lectureScheduleRepository: LectureScheduleRepository,
) {
    fun create(
        year: Int,
        semester: String,
        name: String,
        userId: Long,
    ): CreateTimetableResponse {
        if (name.isBlank()) {
            throw TimetableBlankNameException()
        }
        val timetable =
            timetableRepository.save(
                Timetable(
                    year = year,
                    semester = semester,
                    name = name,
                    userId = userId,
                ),
            )
        return TimetableDto(timetable)
    }

    fun get(userId: Long): GetTimetableResponse {
        val timetables =
            timetableRepository
                .findAllByUserId(userId)
                .sortedWith(
                    compareByDescending<Timetable> { it.year }
                        .thenBy { it.semester }
                        .thenBy { it.name },
                )
        return timetables.map { TimetableDto(it) }
    }

    fun getTimetableDetail(
        id: Long,
        userId: Long,
    ): TimetableDetailResponse {
        val timetable = timetableRepository.findById(id).orElseThrow { TimetableNotFoundException() }
        if (timetable.userId != userId) {
            throw TimetableAccessForbiddenException()
        }
        val timetableLectures = timetableLectureRepository.findAllByTimetableId(id)
        val lecturesDto =
            timetableLectures.map {
                val lecture = lectureRepository.findById(it.lectureId).orElseThrow { LectureNotFoundException(it.lectureId) }
                LectureDto(
                    lecture,
                    lectureScheduleRepository.findAllByLectureId(it.lectureId),
                )
            }
        val totalCredit = lecturesDto.sumOf { it.credit }
        return TimetableDetailResponse(
            id = timetable.id!!,
            year = timetable.year,
            semester = timetable.semester,
            name = timetable.name,
            lectures = lecturesDto,
            totalCredit = totalCredit,
        )
    }

    fun updateTimetableName(
        id: Long,
        name: String,
        userId: Long,
    ) {
        val timetable = timetableRepository.findById(id).orElseThrow { TimetableNotFoundException() }
        if (timetable.userId != userId) {
            throw TimetableUpdateForbiddenException()
        }
        if (name.isBlank()) {
            throw TimetableBlankNameException()
        }
        timetable.name = name
        timetableRepository.save(timetable)
    }

    fun deleteTimetable(
        id: Long,
        userId: Long,
    ) {
        val timetable = timetableRepository.findById(id).orElseThrow { TimetableNotFoundException() }
        if (timetable.userId != userId) {
            throw TimetableDeleteForbiddenException()
        }
        timetableLectureRepository.deleteAllByTimetableId(id)
        timetableRepository.delete(timetable)
    }

    fun addLecture(
        timetableId: Long,
        userId: Long,
        lectureId: Long,
    ) {
        val timetable = timetableRepository.findById(timetableId).orElseThrow { TimetableNotFoundException() }
        if (timetable.userId != userId) {
            throw TimetableUpdateForbiddenException()
        }
        lectureRepository.findById(lectureId).orElseThrow { LectureNotFoundException(lectureId) }
        val lectureSchedules = lectureScheduleRepository.findAllByLectureId(lectureId)
        val otherLectures = timetableLectureRepository.findAllByTimetableId(timetableId)
        otherLectures.forEach { relation ->
            val otherLectureSchedules = lectureScheduleRepository.findAllByLectureId(relation.lectureId)
            otherLectureSchedules.forEach { otherSchedule ->
                lectureSchedules.forEach { newSchedule ->
                    if (otherSchedule.dayOfWeek == newSchedule.dayOfWeek) {
                        val overlapped =
                            otherSchedule.startTime < newSchedule.endTime &&
                                newSchedule.startTime < otherSchedule.endTime
                        if (overlapped) {
                            throw LectureOverlapException()
                        }
                    }
                }
            }
        }

        timetableLectureRepository.save(
            TimetableLecture(
                timetableId = timetableId,
                lectureId = lectureId,
            ),
        )
    }

    fun deleteLecture(
        timetableId: Long,
        userId: Long,
        lectureId: Long,
    ) {
        val timetable = timetableRepository.findById(timetableId).orElseThrow { TimetableNotFoundException() }
        if (timetable.userId != userId) {
            throw TimetableUpdateForbiddenException()
        }
        lectureRepository.findById(lectureId).orElseThrow { LectureNotFoundException(lectureId) }
        val relation =
            timetableLectureRepository.findByTimetableIdAndLectureId(timetableId, lectureId)
                ?: throw LectureNotFoundException(lectureId)
        timetableLectureRepository.delete(relation)
    }
}
