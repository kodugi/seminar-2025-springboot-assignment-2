package com.wafflestudio.spring2025.lectureCrawler.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class LectureCrawlerDto(
    @JsonProperty("ltTime")
    val ltTime: List<String> = listOf(),
    @JsonProperty("ltRoom")
    val ltRoom: List<String> = listOf(),
    @JsonProperty("sbjtNm")
    val courseName: String? = null,
    @JsonProperty("sbjtSubhNm")
    val courseSubName: String? = null,
    @JsonProperty("profNm")
    val professorName: String? = null,
    @JsonProperty("departmentKorNm")
    val departmentKorNm: String? = null,
    @JsonProperty("majorKorNm")
    val majorKorNm: String? = null,
    @JsonProperty("cptnCorsFgNm")
    val academicCourse: String? = null,
    @JsonProperty("openShyr")
    val academicYear: String? = null,
)
