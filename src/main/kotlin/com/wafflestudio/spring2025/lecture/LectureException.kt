package com.wafflestudio.spring2025.lecture

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class TimetableException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class LectureNotFoundException(
    lectureId: Long,
) : TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.NOT_FOUND,
        msg = "Lecture not found",
        cause = null,
    )
