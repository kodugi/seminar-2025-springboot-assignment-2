package com.wafflestudio.spring2025.lectureCrawler.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wafflestudio.spring2025.lectureCrawler.dto.LectureCrawlerDto
import org.springframework.core.io.buffer.PooledDataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import org.springframework.web.reactive.function.client.createExceptionAndAwait

@Component
class LectureCrawlerRepository(
    private val sugangSnuWebClient: WebClient,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        const val SEARCH_PAGE_PATH = "/sugang/cc/cc100InterfaceSrch.action" // 세션 받을 경로
        const val SEARCH_POPUP_PATH = "/sugang/cc/cc101ajax.action"
        const val DEFAULT_SEARCH_POPUP_PARAMS = """t_profPersNo=&workType=+&sbjtSubhCd=000"""

        const val LECTURE_EXCEL_DOWNLOAD_PATH = "/sugang/cc/cc100InterfaceExcel.action"
        val DEFAULT_LECTURE_EXCEL_DOWNLOAD_PARAMS =
            """
            seeMore=더보기&
            srchBdNo=&srchCamp=&srchOpenSbjtFldCd=&srchCptnCorsFg=&
            srchCurrPage=1&
            srchExcept=&srchGenrlRemoteLtYn=&srchIsEngSbjt=&
            srchIsPendingCourse=&srchLsnProgType=&srchMrksApprMthdChgPosbYn=&srchMrksGvMthd=&
            srchOpenUpDeptCd=&srchOpenMjCd=&srchOpenPntMax=&srchOpenPntMin=&srchOpenSbjtDayNm=&
            srchOpenSbjtNm=&srchOpenSbjtTm=&srchOpenSbjtTmNm=&srchOpenShyr=&srchOpenSubmattCorsFg=&
            srchOpenSubmattFgCd1=&srchOpenSubmattFgCd2=&srchOpenSubmattFgCd3=&srchOpenSubmattFgCd4=&
            srchOpenSubmattFgCd5=&srchOpenSubmattFgCd6=&srchOpenSubmattFgCd7=&srchOpenSubmattFgCd8=&
            srchOpenSubmattFgCd9=&srchOpenDeptCd=&srchOpenUpSbjtFldCd=&
            srchPageSize=9999&
            srchProfNm=&srchSbjtCd=&srchSbjtNm=&srchTlsnAplyCapaCntMax=&srchTlsnAplyCapaCntMin=&srchTlsnRcntMax=&srchTlsnRcntMin=&
            workType=EX
            """.trimIndent().replace("\n", "")
    }

    private fun semesterToSearchString(semester: String): String {
        return when (semester) {
            "1" -> "U000200001U000300001"
            "2" -> "U000200001U000300002"
            "3" -> "U000200002U000300001"
            "4" -> "U000200002U000300002"
            else -> "U000200001U000300001"
        }
    }

    suspend fun getLectureInfo(
        year: Int,
        semester: String,
        courseNumber: String,
        lectureNumber: String,
    ): LectureCrawlerDto =
        sugangSnuWebClient
            .get()
            .uri { builder ->
                val semesterSearchString = semesterToSearchString(semester)
                builder
                    .path(SEARCH_POPUP_PATH)
                    .query(DEFAULT_SEARCH_POPUP_PARAMS)
                    .queryParam("openSchyy", year)
                    .queryParam("openShtmFg", semesterSearchString.substring(0..9))
                    .queryParam("openDetaShtmFg", semesterSearchString.substring(10))
                    .queryParam("sbjtCd", courseNumber)
                    .queryParam("ltNo", lectureNumber)
                    .build()
            }.accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody<String>()
            .let { objectMapper.readValue<LectureCrawlerDto>(it) }

    /**
     * 세션(쿠키)을 받기 위한 워밍업 요청
     * @return 'Set-Cookie' 헤더 리스트
     */
    suspend fun establishSession(): List<String> {
        return sugangSnuWebClient
            .get()
            .uri(SEARCH_PAGE_PATH)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Whale/4.34.340.19 Safari/537.36")
            .awaitExchange { response ->
                // 'Set-Cookie' 헤더에 있는 모든 쿠키 값을 리스트로 반환
                response.headers().header(HttpHeaders.SET_COOKIE)
            }
    }

    /**
     * 엑셀 파일을 다운로드 (쿠키를 수동으로 전달받음)
     */
    suspend fun downloadLecturesExcel(
        year: Int,
        semester: String,
        language: String = "ko",
        cookies: List<String> // <-- 세션에서 받아온 쿠키
    ): PooledDataBuffer {
        val formData = LinkedMultiValueMap<String, String>()

        DEFAULT_LECTURE_EXCEL_DOWNLOAD_PARAMS.split("&").forEach {
            val parts = it.split("=", limit = 2)
            if (parts.size == 2) {
                formData.add(parts[0], parts[1])
            }
        }

        formData.add("srchLanguage", language)
        formData.add("srchOpenSchyy", year.toString())
        formData.add("srchOpenShtm", semesterToSearchString(semester))

        return sugangSnuWebClient
            .post()
            .uri(LECTURE_EXCEL_DOWNLOAD_PATH)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            // cURL에서 복사한 모든 헤더
            .accept(
                MediaType.TEXT_HTML,
                MediaType.APPLICATION_XHTML_XML,
                MediaType.APPLICATION_XML,
                MediaType.parseMediaType("image/avif"),
                MediaType.parseMediaType("image/webp"),
                MediaType.parseMediaType("image/apng"),
                MediaType.parseMediaType("*/*")
            )
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Whale/4.34.340.19 Safari/537.36")
            .header("Referer", "https://sugang.snu.ac.kr/sugang/cc/cc100InterfaceSrch.action")
            .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
            .header("Origin", "https://sugang.snu.ac.kr")
            .header("Sec-Fetch-Dest", "document")
            .header("Sec-Fetch-Mode", "navigate")
            .header("Sec-Fetch-Site", "same-origin")
            .header("Upgrade-Insecure-Requests", "1")
            // 받아온 쿠키 리스트를 하나의 'Cookie' 헤더로 합쳐서 전송
            .header(HttpHeaders.COOKIE, cookies.joinToString(separator = "; "))
            .body(BodyInserters.fromFormData(formData))
            .awaitExchange {
                if (it.statusCode().is2xxSuccessful) {
                    it.awaitBody()
                } else {
                    throw it.createExceptionAndAwait()
                }
            }
    }
}