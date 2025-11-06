package com.wafflestudio.spring2025.lectureCrawler.dto

data class LectureCrawlerResponse(
    val message: String,
    val count: Int,
    val year: Int,
    val semester: String,
)
