package com.wafflestudio.spring2025.lectureCrawler.service

import com.wafflestudio.spring2025.lectureCrawler.repository.LectureCrawlerRepository
import com.wafflestudio.spring2025.lecture.model.Lecture
import com.wafflestudio.spring2025.lecture.model.LectureSchedule
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.lecture.repository.LectureScheduleRepository
import com.wafflestudio.spring2025.lectureCrawler.util.LectureTimeConverter
import org.apache.poi.hssf.usermodel.HSSFWorkbook // <-- .xls (OLE2) 용
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
        // 1. 엑셀을 파싱 (중복된 Lecture 객체 포함)
        val lecturesAndScheduleInfos = getLecturesAndScheduleInfos(year, semester)

        // 2. (DuplicateKeyException 해결) "교과목번호 + 강좌번호"를 기준으로 그룹화 (trim() 포함)
        val groupedByLecture = lecturesAndScheduleInfos.groupBy {
            (it.first.courseNumber + it.first.lectureNumber).trim()
        }

        // 3. 각 그룹의 *첫 번째* Lecture 객체만 저장
        val uniqueLecturesToSave = groupedByLecture.values.map { group ->
            group.first().first
        }

        // 4. *중복이 제거된* Lecture 리스트를 DB에 저장 (ID 부여됨)
        lectureRepository.saveAll(uniqueLecturesToSave)

        // 5. 그룹화된 데이터를 기반으로 Schedule 리스트 생성
        val schedulesToSave = groupedByLecture.values.flatMap { group ->

            val savedLecture = group.first().first
            val lectureId = savedLecture.id!! // NullPointerException 해결

            // 그룹 내 *모든* ScheduleInfo를 순회
            group.flatMap { (_, scheduleInfo) ->
                lectureTimeConverter.parseLectureTimes(scheduleInfo.classTimeText).map { time ->
                    // "월" -> 1 변환
                    val dayOfWeekAsInt = convertDayOfWeekToInt(time.dayOfWeek)
                    LectureSchedule(
                        lectureId = lectureId,
                        dayOfWeek = dayOfWeekAsInt, // NumberFormatException 해결
                        startTime = time.startTime,
                        endTime = time.endTime,
                        place = scheduleInfo.location,
                    )
                }
            }
        }

        // 6. 모든 Schedule 저장
        lectureScheduleRepository.saveAll(schedulesToSave)

        // 7. 중복 제거된 강의 개수 반환
        return uniqueLecturesToSave.size
    }

    // "월", "화" -> 1, 2 변환 헬퍼 함수
    private fun convertDayOfWeekToInt(day: String): Int {
        return when (day.trim()) {
            "월" -> 1
            "화" -> 2
            "수" -> 3
            "목" -> 4
            "금" -> 5
            "토" -> 6
            "일" -> 7
            else -> 0
        }
    }

    // Schedule 생성을 위한 임시 DTO
    private data class ScheduleInfo(
        val classTimeText: String,
        val location: String
    )

    // 세션 요청 및 쿠키 전달
    private suspend fun getLecturesAndScheduleInfos(
        year: Int,
        semester: String,
    ): List<Pair<Lecture, ScheduleInfo>> {

        // 1. (수정) 엑셀 다운로드 전에 세션부터 받도록 호출
        val sessionCookies = lectureCrawlerRepository.establishSession()

        // 2. (수정) 엑셀 다운로드 시 쿠키 리스트를 전달
        val koreanLectureXlsx = lectureCrawlerRepository.downloadLecturesExcel(year, semester, "ko", sessionCookies)
        val englishLectureXlsx = lectureCrawlerRepository.downloadLecturesExcel(year, semester, "en", sessionCookies)

        // (수정) HSSFWorkbook 사용
        val koreanSheet = HSSFWorkbook(koreanLectureXlsx.asInputStream()).getSheetAt(0)
        val englishSheet = HSSFWorkbook(englishLectureXlsx.asInputStream()).getSheetAt(0)
        val fullSheet =
            koreanSheet.zip(englishSheet).map { (koreanRow, englishRow) ->
                koreanRow + englishRow
            }

        // (수정) 헤더 trim()
        val columnNameIndex = fullSheet[2].associate { it.stringCellValue.trim() to it.columnIndex }

        return fullSheet
            .drop(3)
            .map { row ->
                convertRowToLectureAndScheduleInfo(row, columnNameIndex, year, semester)
            }.also {
                koreanLectureXlsx.release()
                englishLectureXlsx.release()
            }
    }

    // 엑셀 셀 값 trim()
    private fun convertRowToLectureAndScheduleInfo(
        row: List<Cell>,
        columnNameIndex: Map<String, Int>,
        year: Int,
        semester: String,
    ): Pair<Lecture, ScheduleInfo> {

        // (수정) 엑셀에서 읽는 모든 문자열 값을 trim()
        fun List<Cell>.getCellByColumnName(key: String): String? =
            columnNameIndex[key]?.let { index ->
                this.getOrNull(index)?.stringCellValue?.trim() // <-- trim() 추가!
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
        val instructor = row.getCellByColumnName("주담당교수") ?: ""

        val classTimeText = row.getCellByColumnName("수업교시") ?: ""
        val location = row.getCellByColumnName("강의실(동-호)(#연건, *평창)") ?: ""

        val lecture = Lecture(
            year = year,
            semester = semester,
            category = category,
            college = college,
            department = department?.replace("null", "")?.ifEmpty { college } ?: college,
            academicCourse = academicCourse,
            academicYear = if (academicCourse != "학사") academicCourse else academicYear,
            courseNumber = courseNumber, // 이미 trim된 값이 들어옴
            lectureNumber = lectureNumber, // 이미 trim된 값이 들어옴
            courseTitle = lectureTitle,
            credit = credit,
            courseSubtitle = lectureSubtitle,
            instructor = instructor,
        )

        val scheduleInfo = ScheduleInfo(classTimeText, location)

        return Pair(lecture, scheduleInfo)
    }
}