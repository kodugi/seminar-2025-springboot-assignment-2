package com.wafflestudio.spring2025.timetable.controller

import com.wafflestudio.spring2025.timetable.dto.AddLectureRequest
import com.wafflestudio.spring2025.timetable.dto.CreateTimetableRequest
import com.wafflestudio.spring2025.timetable.dto.CreateTimetableResponse
import com.wafflestudio.spring2025.timetable.dto.DeleteLectureRequest
import com.wafflestudio.spring2025.timetable.dto.GetTimetableResponse
import com.wafflestudio.spring2025.timetable.dto.TimetableDetailResponse
import com.wafflestudio.spring2025.timetable.dto.UpdateTimetableNameRequest
import com.wafflestudio.spring2025.timetable.service.TimetableService
import com.wafflestudio.spring2025.user.AuthenticateException
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TimetableController(
    private val timetableService: TimetableService,
) {
    @PostMapping("api/v1/timetables")
    @Operation(
        summary = "시간표 생성",
        description = "시간표의 연도, 학기, 이름을 입력받아 새로운 시간표 생성"
    )
    fun create(
        @LoggedInUser user: User,
        @RequestBody createRequest: CreateTimetableRequest,
    ): ResponseEntity<CreateTimetableResponse> {
        val userId = user.id ?: throw AuthenticateException()
        val timetableDto =
            timetableService.create(
                year = createRequest.year,
                semester = createRequest.semester,
                name = createRequest.name,
                userId = userId,
            )
        return ResponseEntity.ok(timetableDto)
    }

    @GetMapping("api/v1/timetables")
    @Operation(
        summary = "시간표 조회",
        description = "본인이 생성한 모든 시간표 조회"
    )
    fun getTimetables(
        @LoggedInUser user: User,
    ): ResponseEntity<GetTimetableResponse> {
        val userId = user.id ?: throw AuthenticateException()
        val response = timetableService.get(userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("api/v1/timetables/{id}")
    @Operation(
        summary = "시간표 상세 조회",
        description = "시간표 Id를 입력받아 해당 시간표의 기본 정보 및 강의 시간과 장소 조회"
    )
    fun getTimetableDetail(
        @PathVariable id: Long,
        @LoggedInUser user: User,
    ): ResponseEntity<TimetableDetailResponse> {
        val userId = user.id ?: throw AuthenticateException()
        return ResponseEntity.ok(timetableService.getTimetableDetail(id, userId))
    }

    @PatchMapping("api/v1/timetables/{id}")
    @Operation(
        summary = "시간표 이름 변경",
        description = "시간표의 Id와 새 이름을 입력받아 시간표 이름 변경"
    )
    fun updateTimetable(
        @PathVariable id: Long,
        @LoggedInUser user: User,
        @RequestBody updateRequest: UpdateTimetableNameRequest,
    ): ResponseEntity<Unit> {
        val userId = user.id ?: throw AuthenticateException()
        timetableService.updateTimetableName(id, updateRequest.name, userId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("api/v1/timetables/{id}")
    @Operation(
        summary = "시간표 삭제",
        description = "시간표 Id를 입력받아 해당 시간표 삭제"
    )
    fun deleteTimetable(
        @PathVariable id: Long,
        @LoggedInUser user: User,
    ): ResponseEntity<Unit> {
        val userId = user.id ?: throw AuthenticateException()
        timetableService.deleteTimetable(id, userId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("api/v1/timetables/{id}/lectures")
    @Operation(
        summary = "시간표에 강의 추가",
        description = "시간표 Id와 강의 Id를 입력받아 시간표에 강의 추가"
    )
    fun addLecture(
        @PathVariable id: Long,
        @LoggedInUser user: User,
        @RequestBody addLectureRequest: AddLectureRequest,
    ): ResponseEntity<Unit> {
        val userId = user.id ?: throw AuthenticateException()
        timetableService.addLecture(id, userId, addLectureRequest.lectureId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("api/v1/timetables/{id}/lectures")
    @Operation(
        summary = "시간표에서 강의 삭제",
        description = "시간표 Id와 강의 Id를 입력받아 시간표에서 강의 삭제"
    )
    fun deleteLecture(
        @PathVariable id: Long,
        @LoggedInUser user: User,
        @RequestBody deleteLectureRequest: DeleteLectureRequest,
    ): ResponseEntity<Unit> {
        val userId = user.id ?: throw AuthenticateException()
        timetableService.deleteLecture(id, userId, deleteLectureRequest.lectureId)
        return ResponseEntity.ok().build()
    }
}
