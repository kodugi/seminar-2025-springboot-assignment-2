package com.wafflestudio.spring2025.timetable

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class TimetableException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class TimetableNotFoundException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.NOT_FOUND,
        msg = "Timetable not found",
    )

class TimetableBlankNameException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.BAD_REQUEST,
        msg = "Name must not be blank",
    )

class TimetableUpdateForbiddenException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.FORBIDDEN,
        msg = "You don't have permission to update this timetable",
    )

class TimetableDeleteForbiddenException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.FORBIDDEN,
        msg = "You don't have permission to delete this timetable",
    )

class TimetableAccessForbiddenException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.FORBIDDEN,
        msg = "You don't have permission to access this timetable",
    )

class LectureOverlapException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.CONFLICT,
        msg = "The lecture has overlapping schedule with other lectures"
    )