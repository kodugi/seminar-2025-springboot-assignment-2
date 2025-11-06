package com.wafflestudio.spring2025

import com.wafflestudio.spring2025.lectureCrawler.service.LectureCrawlerService
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class LectureCrawlerServiceTest {

    @Autowired
    private lateinit var lectureCrawlerService: LectureCrawlerService

    @Test
    fun `should crawl lectures from SNU site`() = runBlocking {
        val year = 2025
        val semester = "1"
        val count = lectureCrawlerService.crawlLectures(year, semester)
        assertThat(count).isGreaterThanOrEqualTo(0)
        println("Crawled lecture count: $count")
    }
}