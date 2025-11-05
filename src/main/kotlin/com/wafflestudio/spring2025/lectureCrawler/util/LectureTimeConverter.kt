package com.wafflestudio.spring2025.lectureCrawler.util

import com.wafflestudio.spring2025.lectureCrawler.dto.LectureTimeDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalTime
import kotlin.text.get

@Component
object LectureTimeConverter {
    private val log = LoggerFactory.getLogger(javaClass)
    private val timeRegEx =
        """^(?<day>[월화수목금토일])\((?<startTime>\d{2}:\d{2})~(?<endTime>\d{2}:\d{2})\)$""".toRegex()

    val dayAndTimeContentRegex = """^(?<day>[월화수목금토일])\((?<times>.+)\)$""".toRegex()

    val timeBlockRegex = """(?<startTime>\d{2}:\d{2})~(?<endTime>\d{2}:\d{2})""".toRegex()

    fun parseLectureTimes(input: String): List<LectureTimeDto> {
        val dayMatch = dayAndTimeContentRegex.find(input)
            ?: return emptyList()

        val day = dayMatch.groups["day"]!!.value
        val timesString = dayMatch.groups["times"]?.value
            ?: return emptyList()
        return timeBlockRegex.findAll(timesString)
            .mapNotNull { timeMatch ->
                try {
                    val startTime = LocalTime.parse(timeMatch.groups["startTime"]!!.value)
                    val endTime = LocalTime.parse(timeMatch.groups["endTime"]!!.value)

                    LectureTimeDto(
                        dayOfWeek = day,
                        startTime = startTime,
                        endTime = endTime
                    )
                } catch (e: Exception) {
                    null
                }
            }
            .toList()
    }
}