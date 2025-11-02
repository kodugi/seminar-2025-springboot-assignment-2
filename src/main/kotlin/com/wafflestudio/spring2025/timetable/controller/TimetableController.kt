package com.wafflestudio.spring2025.timetable.controller

import com.wafflestudio.spring2025.timetable.dto.AddLectureRequest
import com.wafflestudio.spring2025.timetable.dto.CreateTimetableRequest
import com.wafflestudio.spring2025.timetable.dto.DeleteLectureRequest
import com.wafflestudio.spring2025.timetable.dto.GetTimetableResponse
import com.wafflestudio.spring2025.timetable.dto.TimetableDetailResponse
import com.wafflestudio.spring2025.timetable.dto.UpdateTimetableNameRequest
import com.wafflestudio.spring2025.timetable.service.TimetableService
import com.wafflestudio.spring2025.user.AuthenticateException
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
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
    fun create(
        @LoggedInUser user: User,
        @RequestBody createRequest: CreateTimetableRequest,
    ): ResponseEntity<CreateTimetableRequest> {
        val userId = user.id?:throw AuthenticateException()
        val timetableDto = timetableService.create(
            year = createRequest.year,
            semester = createRequest.semester,
            name = createRequest.name,
            userId = userId
        )
        return ResponseEntity.ok(createRequest)
    }

    @GetMapping("api/v1/timetables")
    fun geetTimetables(
        @LoggedInUser user: User,
    ): ResponseEntity<GetTimetableResponse> {
        val userId = user.id?:throw AuthenticateException()
        val getTimetableResponse = timetableService.get(userId)
        return ResponseEntity.ok(getTimetableResponse)
    }

    @GetMapping("api/v1/timetables/{id}")
    fun getTimetableDetail(@PathVariable id: Long): ResponseEntity<TimetableDetailResponse> {
        return ResponseEntity.ok(timetableService.getTimetableDetail(id))
    }

    @PatchMapping("api/v1/timetables/{id}")
    fun updateTimetable(
        @PathVariable id: Long,
        @LoggedInUser user: User,
        @RequestBody updateRequest: UpdateTimetableNameRequest,
    ): ResponseEntity<Unit> {
        val userId = user.id?:throw AuthenticateException()
        timetableService.updateTimetableName(id, updateRequest.name, userId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("api/v1/timetables/{id}")
    fun deleteTimetable(
        @PathVariable id: Long,
        @LoggedInUser user: User,
    ): ResponseEntity<Unit> {
        val userId = user.id?:throw AuthenticateException()
        timetableService.deleteTimetable(id, userId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("api/v1/timetables/{id}/lectures")
    fun addLecture(
        @PathVariable id: Long,
        @LoggedInUser user: User,
        @RequestBody addLectureRequest: AddLectureRequest,
    ): Unit {
        val userId = user.id?:throw AuthenticateException()
        timetableService.addLecture(id, userId, addLectureRequest.lectureId)
    }

    @DeleteMapping("api/v1/timetables/{id}/lectures")
    fun deleteLecture(
        @PathVariable id: Long,
        @LoggedInUser user: User,
        @RequestBody deleteLectureRequest: DeleteLectureRequest,
    ): Unit {
        val userId = user.id?:throw AuthenticateException()
        timetableService.deleteLecture(id, userId, deleteLectureRequest.lectureId)
    }
}