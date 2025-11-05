package com.wafflestudio.spring2025.lectureCrawler.controller

import com.wafflestudio.spring2025.lectureCrawler.service.LectureCrawlerService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@SecurityRequirement(name = "bearerAuth")
class LectureCrawlerController(
    private val lectureCrawlerService: LectureCrawlerService,
) {
    fun getYearAndSemester(semester: String): Pair<Int, String> {
        val year = semester.substring(0,semester.length-2).toInt()
        val semester = semester.substring(semester.length-1)
        return Pair(year, semester)
    }

    @PostMapping("/api/v1/lectures/fetch")
    suspend fun crawlLectures(
        @RequestParam(defaultValue = "2025-1")
        semester: String,
    ): ResponseEntity<Map<String, Any>> {
        val (year, semester) = getYearAndSemester(semester)
        val count = lectureCrawlerService.crawlLectures(year, semester)
        return ResponseEntity.ok(
            mapOf(
                "message" to "Crawling successful",
                "semester" to semester,
                "year" to year,
                "count" to count,
            ),
        )
    }
}