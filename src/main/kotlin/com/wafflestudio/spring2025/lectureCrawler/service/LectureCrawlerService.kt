package com.wafflestudio.spring2025.lectureCrawler.service

import com.wafflestudio.spring2025.lectureCrawler.repository.LectureCrawlerRepository
import com.wafflestudio.spring2025.lecture.model.Lecture
import com.wafflestudio.spring2025.lecture.model.LectureSchedule
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.lecture.repository.LectureScheduleRepository
import com.wafflestudio.spring2025.lectureCrawler.util.LectureTimeConverter
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.springframework.stereotype.Service

@Service
class LectureCrawlerService(
    private val lectureRepository: LectureRepository,
    private val lectureScheduleRepository: LectureScheduleRepository,
    private val lectureCrawlerRepository: LectureCrawlerRepository,
    private val lectureTimeConverter: LectureTimeConverter,
) {
    suspend fun crawlLectures(
        year: Int,
        semester: String,
    ): Int {
        val lecturesAndSchedules = getLecturesAndSchedules(year, semester)
        for (lectureAndSchedules in lecturesAndSchedules) {
            val lecture = lectureAndSchedules.first
            val schedules = lectureAndSchedules.second
            lectureRepository.save(lecture)
            lectureScheduleRepository.saveAll(schedules)
        }
        return lecturesAndSchedules.size
    }

    private suspend fun getLecturesAndSchedules(
        year: Int,
        semester: String,
    ): List<Pair<Lecture, List<LectureSchedule>>> {
        val koreanLectureXlsx = lectureCrawlerRepository.downloadLecturesExcel(year, semester, "ko")
        val englishLectureXlsx = lectureCrawlerRepository.downloadLecturesExcel(year, semester, "en")

        val koreanSheet = HSSFWorkbook(koreanLectureXlsx.asInputStream()).getSheetAt(0)
        val englishSheet = HSSFWorkbook(englishLectureXlsx.asInputStream()).getSheetAt(0)
        val fullSheet =
            koreanSheet.zip(englishSheet).map { (koreanRow, englishRow) ->
                koreanRow + englishRow
            }

        val columnNameIndex = fullSheet[2].associate { it.stringCellValue to it.columnIndex }

        return fullSheet
            .drop(3)
            .map { row ->
                convertRowToLectureAndSchedules(row, columnNameIndex, year, semester)
            }.also {
                koreanLectureXlsx.release()
                englishLectureXlsx.release()
            }
    }

    private fun convertRowToLectureAndSchedules(
        row: List<Cell>,
        columnNameIndex: Map<String, Int>,
        year: Int,
        semester: String,
    ): Pair<Lecture, List<LectureSchedule>> {
        fun List<Cell>.getCellByColumnName(key: String): String? =
            columnNameIndex[key]?.let { index ->
                this.getOrNull(index)?.stringCellValue
            }

        val category = row.getCellByColumnName("교과구분")
        val college = row.getCellByColumnName("개설대학")
        val department = row.getCellByColumnName("개설학과")
        val academicCourse = row.getCellByColumnName("이수과정")
        val academicYear = row.getCellByColumnName("학년")
        val courseNumber = row.getCellByColumnName("교과목번호") ?: throw IllegalArgumentException("필수 컬럼 '교과목번호'가 없습니다")
        val lectureNumber = row.getCellByColumnName("강좌번호") ?: throw IllegalArgumentException("필수 컬럼 '강좌번호'가 없습니다")
        val lectureTitle = row.getCellByColumnName("교과목명") ?: throw IllegalArgumentException("필수 컬럼 '교과목명'이 없습니다")
        val lectureSubtitle = row.getCellByColumnName("부제명")
        val credit = row.getCellByColumnName("학점")?.toIntOrNull() ?: 0
        val classTimeText = row.getCellByColumnName("수업교시") ?: ""
        val location = row.getCellByColumnName("강의실(동-호)(#연건, *평창)") ?: ""
        val instructor = row.getCellByColumnName("주담당교수") ?: ""

        val lectureFullTitle = if (lectureSubtitle.isNullOrEmpty()) lectureTitle else "$lectureTitle ($lectureSubtitle)"

        val lecture = Lecture(
            year = year,
            semester = semester,
            category = category,
            college = college,
            department = department?.replace("null", "")?.ifEmpty { college } ?: college,
            academicCourse = academicCourse,
            academicYear = if (academicCourse != "학사") academicCourse else academicYear,
            courseNumber = courseNumber,
            lectureNumber = lectureNumber,
            courseTitle = lectureTitle,
            credit = credit,
            courseSubtitle = lectureSubtitle,
            instructor = instructor,
        )
        val schedules = lectureTimeConverter.parseLectureTimes(classTimeText).map {
            LectureSchedule(
                lectureId = lecture.id!!,
                dayOfWeek = it.dayOfWeek.toInt(),
                startTime = it.startTime,
                endTime = it.endTime,
                place = location,
            )
        }
        return Pair(lecture, schedules)
    }
}