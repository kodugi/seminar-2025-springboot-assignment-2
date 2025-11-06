package com.wafflestudio.spring2025.lecture.controller

import com.wafflestudio.spring2025.lecture.dto.LectureSearchResponse
import com.wafflestudio.spring2025.lecture.service.LectureService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LectureController(
    private val lectureService: LectureService,
) {
    @GetMapping("api/v1/lectures")
    @Operation(
        summary = "강의 검색",
        description = "연도와 학기, 키워드를 바탕으로 강의 검색"
    )
    fun search(
        @RequestParam("year") year: Int,
        @RequestParam("semester") semester: String,
        @RequestParam("keyword") keyword: String,
        pageable: Pageable,
    ): LectureSearchResponse = lectureService.search(year, semester, keyword, pageable)
}
