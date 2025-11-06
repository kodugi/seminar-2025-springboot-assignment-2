package com.wafflestudio.spring2025.user.controller

import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.dto.GetMeResponse
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController {
    @GetMapping("/me")
    @Operation(
        summary = "사용자 정보 조회",
        description = "본인의 정보 조회"
    )
    fun me(
        @LoggedInUser user: User,
    ): ResponseEntity<GetMeResponse> = ResponseEntity.ok(UserDto(user))
}
