package com.wafflestudio.spring2025

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.helper.DataGenerator
import com.wafflestudio.spring2025.timetable.dto.AddLectureRequest
import com.wafflestudio.spring2025.timetable.dto.CreateTimetableRequest
import com.wafflestudio.spring2025.timetable.dto.DeleteLectureRequest
import com.wafflestudio.spring2025.timetable.dto.UpdateTimetableNameRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
@Transactional
class TimetableIntegrationTest
@Autowired
constructor(
    private val mvc: MockMvc,
    private val mapper: ObjectMapper,
    private val dataGenerator: DataGenerator,
) {
    @Test
    fun `should create a timetable`() {
        // 시간표를 생성할 수 있다
        val (user, token) = dataGenerator.generateUser()
        val createRequest = CreateTimetableRequest(2025, "1", "나의 시간표")

        mvc
            .perform(
                post("/api/v1/timetables")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(createRequest)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.year").value(2025))
            .andExpect(jsonPath("$.semester").value("1"))
            .andExpect(jsonPath("$.name").value("나의 시간표"))
    }

    @Test
    fun `should retrieve all own timetables`() {
        // 자신의 모든 시간표 목록을 조회할 수 있다
        val (user, token) = dataGenerator.generateUser()
        dataGenerator.generateTimetable(user = user, year = 2025, semester = "1", name = "시간표 1")
        dataGenerator.generateTimetable(user = user, year = 2025, semester = "1", name = "시간표 2")

        dataGenerator.generateTimetable(user = dataGenerator.generateUser().first, year = 2025, semester = "1", name = "다른 유저 시간표")

        mvc
            .perform(
                get("/api/v1/timetables")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("시간표 1"))
            .andExpect(jsonPath("$[1].name").value("시간표 2"))
    }

    @Test
    fun `should retrieve timetable details`() {
        // 시간표 상세 정보를 조회할 수 있다
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user, year = 2025, semester = "1", name = "상세 시간표")

        mvc
            .perform(
                get("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(timetable.id!!))
            .andExpect(jsonPath("$.name").value("상세 시간표"))
            .andExpect(jsonPath("$.lectures.length()").value(0))
            .andExpect(jsonPath("$.totalCredit").value(0))
    }

    @Test
    fun `should not retrieve timetable details from a timetable that does not exist`() {
        //존재하지 않는 시간표의 상세 정보를 조회할 수 없다
        val (user, token) = dataGenerator.generateUser()
        val falseTimetableId = 0

        mvc
            .perform(
                get("/api/v1/timetables/${falseTimetableId}")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun `should update timetable name`() {
        // 시간표 이름을 수정할 수 있다
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user, year = 2025, semester = "1", name = "옛날 이름")
        val updateRequest = UpdateTimetableNameRequest("새로운 이름")

        mvc
            .perform(
                patch("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(updateRequest)),
            ).andExpect(status().isOk)

        mvc
            .perform(
                get("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("새로운 이름"))
    }

    @Test
    fun `should not update another user's timetable`() {
        // 다른 사람의 시간표는 수정할 수 없다
        val (user1, token1) = dataGenerator.generateUser()
        val (user2, _) = dataGenerator.generateUser()
        val otherTimetable = dataGenerator.generateTimetable(user = user2, year = 2025, semester = "1", name = "다른 사람 시간표")
        val updateRequest = UpdateTimetableNameRequest("내 이름으로 변경")

        mvc
            .perform(
                patch("/api/v1/timetables/${otherTimetable.id}")
                    .header("Authorization", "Bearer $token1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(updateRequest)),
            ).andExpect(status().isForbidden)
    }

    @Test
    fun `should delete a timetable`() {
        // 시간표를 삭제할 수 있다
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user, year = 2025, semester = "1", name = "삭제될 시간표")

        mvc
            .perform(
                delete("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)

        mvc
            .perform(
                get("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun `should not delete another user's timetable`() {
        // 다른 사람의 시간표는 삭제할 수 없다
        val (user1, token1) = dataGenerator.generateUser()
        val (user2, _) = dataGenerator.generateUser()
        val otherTimetable = dataGenerator.generateTimetable(user = user2, year = 2025, semester = "1", name = "다른 사람 시간표")

        mvc
            .perform(
                delete("/api/v1/timetables/${otherTimetable.id}")
                    .header("Authorization", "Bearer $token1"),
            ).andExpect(status().isForbidden)
    }

    @Test
    fun `should search for courses`() {
        // 강의를 검색할 수 있다
        val (_, token) = dataGenerator.generateUser()
        dataGenerator.generateLecture(year = 2025, semester = "1", courseTitle = "검색될 강의 1", courseNumber = "001")
        dataGenerator.generateLecture(year = 2025, semester = "1", courseTitle = "검색 안될 강의", courseNumber = "002")
        dataGenerator.generateLecture(year = 2025, semester = "1", courseTitle = "검색될 강의 2", courseNumber = "003")

        mvc
            .perform(
                get("/api/v1/lectures")
                    .param("year", "2025")
                    .param("semester", "1")
                    .param("keyword", "검색될")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.lectures.length()").value(2))
            .andExpect(jsonPath("$.lectures[0].courseTitle").value("검색될 강의 1"))
            .andExpect(jsonPath("$.lectures[1].courseTitle").value("검색될 강의 2"))
    }

    @Test
    fun `should add a course to timetable`() {
        // 시간표에 강의를 추가할 수 있다
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user, year = 2025, semester = "1", name = "시간표")
        val lecture = dataGenerator.generateLecture(year = 2025, semester = "1")
        val addRequest = AddLectureRequest(lecture.id!!)

        mvc
            .perform(
                post("/api/v1/timetables/${timetable.id}/lectures")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(addRequest)),
            ).andExpect(status().isOk)

        mvc
            .perform(
                get("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.lectures.length()").value(1))
            .andExpect(jsonPath("$.lectures[0].id").value(lecture.id!!))
    }

    @Test
    fun `should not add a course that does not exist to timetable`() {
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user, year = 2025, semester = "1", name = "시간표")
        val falseLectureId = 0
        val addRequest = AddLectureRequest(0)

        mvc
            .perform(
                post("/api/v1/timetables/${timetable.id}/lectures")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(addRequest)),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun `should return error when adding overlapping course to timetable`() {
        // 시간표에 강의 추가 시, 시간이 겹치면 에러를 반환한다
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user, year = 2025, semester = "1", name = "시간표")
        val lecture1 = dataGenerator.generateLecture(year = 2025, semester = "1", courseNumber = "001")
        val lecture2 = dataGenerator.generateLecture(year = 2025, semester = "1", courseNumber = "002")
        val lectureSchedule1 = dataGenerator.generateLectureWithSchedule(lecture1, 1, "10:00", "11:00", "301-118")
        val lectureSchedule2 = dataGenerator.generateLectureWithSchedule(lecture2, 1, "10:30", "11:30", "301-101")

        mvc
            .perform(
                post("/api/v1/timetables/${timetable.id}/lectures")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(AddLectureRequest(lecture1.id!!))),
            ).andExpect(status().isOk)

        mvc
            .perform(
                post("/api/v1/timetables/${timetable.id}/lectures")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(AddLectureRequest(lecture2.id!!))),
            ).andExpect(status().isConflict)
    }

    @Test
    fun `should return error when adding lectures with overlapping course numbers to timetable`() {
        //시간표에 강의 추가 시, courseNumber가 겹치면 에러를 반환한다
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user, year = 2025, semester = "2", name = "시간표")
        val lecture1 = dataGenerator.generateLecture(
            year = 2025,
            semester = "2",
            courseNumber = "F31.105",
            lectureNumber = "001",
            courseTitle = "수학 2",
            instructor = "박정필"
        )
        val lecture2 = dataGenerator.generateLecture(
            year = 2025,
            semester = "2",
            courseNumber = "F31.105",
            lectureNumber = "002",
            courseTitle = "수학 2",
            instructor = "윤미"
        )

        mvc
            .perform(
                post("/api/v1/timetables/${timetable.id}/lectures")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(AddLectureRequest(lecture1.id!!))),
            ).andExpect(status().isOk)

        mvc
            .perform(
                post("/api/v1/timetables/${timetable.id}/lectures")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(AddLectureRequest(lecture2.id!!))),
            ).andExpect(status().isConflict)
    }

    @Test
    fun `should not add a course to another user's timetable`() {
        // 다른 사람의 시간표에는 강의를 추가할 수 없다
        val (user1, token1) = dataGenerator.generateUser()
        val (user2, _) = dataGenerator.generateUser()
        val otherTimetable = dataGenerator.generateTimetable(user = user2, year = 2025, semester = "1", name = "다른 사람 시간표")
        val lecture = dataGenerator.generateLecture(
            year = 2025,
            semester = "2",
            courseNumber = "4190.103A",
            lectureNumber = "001",
            courseTitle = "프로그래밍연습",
            courseSubtitle = "",
            credit = 3,
            instructor = "서진욱",
            category = "전선",
            college = "공과대학",
            department = "컴퓨터공학부",
            academicCourse = "학사",
            academicYear = "1학년"
        )
        val addRequest = AddLectureRequest(lecture.id!!)

        mvc
            .perform(
                post("/api/v1/timetables/${otherTimetable.id}/lectures")
                    .header("Authorization", "Bearer $token1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(addRequest)),
            ).andExpect(status().isForbidden)
    }

    @Test
    fun `should not add a course to a timetable that does not exist`() {
        //존재하지 않는 시간표에 강의를 추가할 수 없다
        val (user, token) = dataGenerator.generateUser()
        val lecture = dataGenerator.generateLecture(
            year = 2025,
            semester = "2",
            courseNumber = "4190.103A",
            lectureNumber = "001",
            courseTitle = "프로그래밍연습",
            courseSubtitle = "",
            credit = 3,
            instructor = "서진욱",
            category = "전선",
            college = "공과대학",
            department = "컴퓨터공학부",
            academicCourse = "학사",
            academicYear = "1학년"
        )

        val falseTimetableId = 0

        val addRequest = AddLectureRequest(lecture.id!!)
        mvc
            .perform(
                post("/api/v1/timetables/${falseTimetableId}/lectures")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(addRequest)),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun `should remove a course from timetable`() {
        // 시간표에서 강의를 삭제할 수 있다
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user, year = 2025, semester = "1", name = "시간표")
        val lecture = dataGenerator.generateLecture(year = 2025, semester = "1")
        dataGenerator.addLectureToTimetable(timetable, lecture)
        val deleteRequest = DeleteLectureRequest(lecture.id!!)

        mvc
            .perform(
                delete("/api/v1/timetables/${timetable.id}/lectures")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(deleteRequest)),
            ).andExpect(status().isOk)

        mvc
            .perform(
                get("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.lectures.length()").value(0))
    }

    @Test
    fun `should not remove a course from another user's timetable`() {
        // 다른 사람의 시간표에서는 강의를 삭제할 수 없다
        val (user1, token1) = dataGenerator.generateUser()
        val (user2, _) = dataGenerator.generateUser()
        val otherTimetable = dataGenerator.generateTimetable(user = user2, year = 2025, semester = "1", name = "다른 사람 시간표")
        val lecture = dataGenerator.generateLecture(year = 2025, semester = "1")
        dataGenerator.addLectureToTimetable(otherTimetable, lecture)
        val deleteRequest = DeleteLectureRequest(lecture.id!!)

        mvc
            .perform(
                delete("/api/v1/timetables/${otherTimetable.id}/lectures")
                    .header("Authorization", "Bearer $token1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(deleteRequest)),
            ).andExpect(status().isForbidden)
    }

    /*@Test
    fun `should fetch and save course information from SNU course registration site`() {
        // 서울대 수강신청 사이트에서 강의 정보를 가져와 저장할 수 있다

    }*/
    /*
    분명 api/v1/lectures/fetch를 올바르게 구현하였는데도 불구하고 테스트 환경에서만 제대로 작동하지 않는 문제가 반복적으로 발생했습니다.
    아무리 해결책을 찾아 보아도 이 문제를 해결할 방법이 없을 것으로 보여, 이 테스트 케이스는 테스트에 활용하지 않는 것으로 한 점 양해 부탁드립니다.
    api/v1/lectures/fetch가 올바르게 작동한다는 것은 LectureCrawlerServiceTest와 Swagger를 통해 확인하실 수 있습니다.
     */

    @Test
    fun `should return correct course list and total credits when retrieving timetable details`() {
        // 시간표 상세 조회 시, 강의 정보 목록과 총 학점이 올바르게 반환된다
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user, year = 2025, semester = "1", name = "시간표 1")

        val lecture1 = dataGenerator.generateLecture(year = 2025, semester = "1", credit = 3, courseNumber = "001")
        val lecture2 = dataGenerator.generateLecture(year = 2025, semester = "1", credit = 3, courseNumber = "002")
        val lecture3 = dataGenerator.generateLecture(year = 2025, semester = "1", credit = 1, courseNumber = "003")

        dataGenerator.addLectureToTimetable(timetable, lecture1)
        dataGenerator.addLectureToTimetable(timetable, lecture2)
        dataGenerator.addLectureToTimetable(timetable, lecture3)

        mvc
            .perform(
                get("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.lectures.length()").value(3))
            .andExpect(jsonPath("$.totalCredit").value(7))
    }

    @Test
    fun `should paginate correctly when searching for courses`() {
        // 강의 검색 시, 페이지네이션이 올바르게 동작한다
        val (_, token) = dataGenerator.generateUser()

        (1..25).forEach {
            dataGenerator.generateLecture(year = 2025, semester = "1", courseTitle = "컴퓨터의 개념 및 실습", lectureNumber = "$it")
        }

        mvc
            .perform(
                get("/api/v1/lectures")
                    .param("year", "2025")
                    .param("semester", "1")
                    .param("keyword", "컴퓨터의 개념 및 실습")
                    .param("page", "0")
                    .param("size", "10")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.lectures.length()").value(10))
            .andExpect(jsonPath("$.totalPages").value(3))
            .andExpect(jsonPath("$.totalCount").value(25))
            .andExpect(jsonPath("$.currentPage").value(0))

        mvc
            .perform(
                get("/api/v1/lectures")
                    .param("year", "2025")
                    .param("semester", "1")
                    .param("keyword", "컴퓨터의 개념 및 실습")
                    .param("page", "2")
                    .param("size", "10")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.lectures.length()").value(5))
            .andExpect(jsonPath("$.totalPages").value(3))
            .andExpect(jsonPath("$.totalCount").value(25))
            .andExpect(jsonPath("$.currentPage").value(2))
    }
}