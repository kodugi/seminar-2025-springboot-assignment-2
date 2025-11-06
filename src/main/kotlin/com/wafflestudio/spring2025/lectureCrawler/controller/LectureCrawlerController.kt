package com.wafflestudio.spring2025.lectureCrawler.controller

import com.wafflestudio.spring2025.lectureCrawler.dto.LectureCrawlerResponse
import com.wafflestudio.spring2025.lectureCrawler.service.LectureCrawlerService
import io.swagger.v3.oas.annotations.Operation
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
        val parts = semester.split("-")
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid semester format: $semester")
        }
        val year = parts[0].toInt()
        val semesterStr = parts[1]
        return Pair(year, semesterStr)
    }

    @Operation(security = [])
    @PostMapping("/api/v1/lectures/fetch")
    suspend fun crawlLectures(
        @RequestParam(defaultValue = "2025-1")
        semester: String,
    ): ResponseEntity<LectureCrawlerResponse> {

        try {
            val (year, semester) = getYearAndSemester(semester)
            val count = lectureCrawlerService.crawlLectures(year, semester)

            return ResponseEntity.ok(
                LectureCrawlerResponse(
                    "Crawling successful",
                    count,
                    year,
                    semester,
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()

            return ResponseEntity.internalServerError().body(
                LectureCrawlerResponse(e.message ?: "Unknown error", 0, 0, "")
            )
        }
    }
}