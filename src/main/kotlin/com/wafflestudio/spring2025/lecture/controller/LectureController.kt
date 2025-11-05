package com.wafflestudio.spring2025.lecture.controller

import com.wafflestudio.spring2025.lecture.dto.LectureSearchResponse
import com.wafflestudio.spring2025.lecture.service.LectureService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LectureController(
    private val lectureService: LectureService,
) {
    @GetMapping("api/v1/lectures")
    fun search(
        @RequestParam("year") year: Int,
        @RequestParam("semester") semester: String,
        @RequestParam("keyword") keyword: String,
        pageable: Pageable,
    ): LectureSearchResponse = lectureService.search(year, semester, keyword, pageable)
}
