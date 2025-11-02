package com.wafflestudio.spring2025.timetable.service

import com.wafflestudio.spring2025.lecture.LectureNotFoundException
import com.wafflestudio.spring2025.lecture.dto.core.LectureDto
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.lecture.repository.LectureScheduleRepository
import com.wafflestudio.spring2025.timetable.LectureOverlapException
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

class TimetableService(
    private val timetableRepository : TimetableRepository,
    private val timetableLectureRepository: TimetableLectureRepository,
    private val lectureRepository: LectureRepository,
    private val lectureScheduleRepository : LectureScheduleRepository
) {
    fun create(year: Int, semester: String, name: String, userId: Long): CreateTimetableResponse {
        if(name.isBlank()){
            throw TimetableBlankNameException()
        }
        val timetable = timetableRepository.save(Timetable(
            year = year,
            semester = semester,
            name = name,
            userId = userId
        ))
        return TimetableDto(timetable)
    }

    fun get(userId: Long): GetTimetableResponse {
        val timetables = timetableRepository.findAllByUserId(userId)
        return timetables.map { TimetableDto(it) }
    }

    fun getTimetableDetail(id: Long): TimetableDetailResponse {
        val timetable = timetableRepository.findById(id).orElseThrow{throw TimetableNotFoundException()}
        val timetableLectures = timetableLectureRepository.findAllByTimetableId(id)
        var totalCredit = 0
        val lecturesDto = timetableLectures.map {
            val lecture = lectureRepository.findById(it.lectureId).orElseThrow { throw LectureNotFoundException(it.lectureId) }
            totalCredit += lecture.credit
            LectureDto(
                lecture,
                lectureScheduleRepository.findAllByLectureId(it.lectureId)
            )
        }
        return TimetableDetailResponse(
            id = timetable.id!!,
            year = timetable.year,
            semester = timetable.semester,
            name = timetable.name,
            lectures = lecturesDto,
            totalCredit = totalCredit,
        )
    }

    fun updateTimetableName(id: Long, name: String, userId: Long): Unit {
        val timetable = timetableRepository.findById(id).orElseThrow{throw TimetableNotFoundException()}
        if(timetable.userId != userId){
            throw TimetableUpdateForbiddenException()
        }
        if(name.isBlank()){
            throw TimetableBlankNameException()
        }
        timetableRepository.save(Timetable(
            year = timetable.year,
            semester = timetable.semester,
            name = name,
            userId = userId
        ))
    }

    fun deleteTimetable(id: Long, userId: Long): Unit {
        val timetable = timetableRepository.findById(id).orElseThrow{throw TimetableNotFoundException()}
        if(timetable.userId != userId){
            throw TimetableDeleteForbiddenException()
        }
        timetableLectureRepository.deleteAllByTimetableId(id)
        timetableRepository.delete(timetable)
    }

    fun addLecture(timetableId: Long, userId: Long, lectureId: Long): Unit {
        val timetable = timetableRepository.findById(timetableId).orElseThrow{throw TimetableNotFoundException()}
        if(timetable.userId != userId){
            throw TimetableUpdateForbiddenException()
        }
        val lecture = lectureRepository.findById(lectureId).orElseThrow{throw LectureNotFoundException(lectureId)}
        val lectureSchedules = lectureScheduleRepository.findAllByLectureId(lectureId)
        val otherLectures = timetableLectureRepository.findAllByTimetableId(timetableId)
        otherLectures.forEach {
            val otherLectureSchedules = lectureScheduleRepository.findAllByLectureId(it.lectureId)
            otherLectureSchedules.forEach {
                val otherSchedule = it
                lectureSchedules.forEach {
                    if(otherSchedule.dayOfWeek == it.dayOfWeek){
                        if(otherSchedule.startTime <= it.startTime && otherSchedule.endTime > it.startTime){
                            throw LectureOverlapException()
                        }
                        if(otherSchedule.startTime < it.endTime && otherSchedule.endTime >= it.endTime){
                            throw LectureOverlapException()
                        }
                    }
                }
            }
        }

        timetableLectureRepository.save(TimetableLecture(
            timetableId = timetableId,
            lectureId = lectureId)
        )
    }

    fun deleteLecture(timetableId: Long, userId: Long, lectureId: Long): Unit {
        val lecture = lectureRepository.findById(timetableId).orElseThrow{throw LectureNotFoundException(lectureId)}
        val lectureSchedule = lectureScheduleRepository.findById(lectureId).orElseThrow{throw LectureNotFoundException(lectureId)}
        if(lectureSchedule.id != userId){
            throw TimetableUpdateForbiddenException()
        }
        timetableLectureRepository.deleteByLectureId(lectureId)
    }
}