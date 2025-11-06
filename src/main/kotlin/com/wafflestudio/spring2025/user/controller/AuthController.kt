package com.wafflestudio.spring2025.user.controller

import com.wafflestudio.spring2025.user.dto.LoginRequest
import com.wafflestudio.spring2025.user.dto.LoginResponse
import com.wafflestudio.spring2025.user.dto.RegisterRequest
import com.wafflestudio.spring2025.user.dto.RegisterResponse
import com.wafflestudio.spring2025.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val userService: UserService,
) {
    @PostMapping("/register")
    @Operation(
        summary = "회원가입",
        description = "사용자 이름과 비밀번호를 입력받아 사용자를 저장"
    )
    fun register(
        @RequestBody registerRequest: RegisterRequest,
    ): ResponseEntity<RegisterResponse> {
        val userDto =
            userService.register(
                username = registerRequest.username,
                password = registerRequest.password,
            )
        return ResponseEntity.ok(userDto)
    }

    @PostMapping("/login")
    @Operation(
        summary = "로그인",
        description = "사용자 이름과 비밀번호를 입력받아 인증 토큰 발급 및 로그인"
    )
    fun login(
        @RequestBody loginRequest: LoginRequest,
    ): ResponseEntity<LoginResponse> {
        val token =
            userService.login(
                username = loginRequest.username,
                password = loginRequest.password,
            )
        return ResponseEntity.ok(LoginResponse(token))
    }
}
