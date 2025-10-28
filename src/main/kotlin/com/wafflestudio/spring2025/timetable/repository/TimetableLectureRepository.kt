package com.wafflestudio.spring2025.timetable.repository

import com.wafflestudio.spring2025.timetable.model.TimetableLecture
import org.springframework.data.repository.ListCrudRepository

interface TimetableLectureRepository : ListCrudRepository<TimetableLecture, Long> {

    /**
     * 특정 시간표(timetableId)에 포함된 모든 강의 연결 정보를 조회한다.
     * (시간표 상세 조회 시 사용)
     * @param timetableId 시간표 ID
     * @return List<TimetableLecture> 해당 시간표에 속한 모든 연결 정보 목록
     */
    fun findAllByTimetableId(timetableId: Long): List<TimetableLecture>

    /**
     * 특정 시간표(timetableId)에서 특정 강의(lectureId)의 연결 정보를 조회한다.
     * (시간표에서 강의 삭제 시, 해당 연결 정보를 찾아 삭제하기 위해 사용)
     * @param timetableId 시간표 ID
     * @param lectureId 강의 ID
     * @return TimetableLecture? 해당 연결 정보 (없으면 null)
     */
    fun findByTimetableIdAndLectureId(timetableId: Long, lectureId: Long): TimetableLecture?

    /**
     * 특정 시간표(timetableId)에 포함된 모든 강의 연결 정보를 삭제한다.
     * @param timetableId 시간표 ID
     */
    fun deleteAllByTimetableId(timetableId: Long)
}