package com.wafflestudio.spring2025.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "My Timetable API", // 1. API 문서 제목
        version = "v1",             // 2. API 버전
        description = "시간표 및 강의 API 명세서" // 3. API에 대한 설명
    )
)
@SecurityScheme(
    name = "BearerAuth", // 4. Spring Security에서 사용할 이름 (임의 지정)
    type = SecuritySchemeType.HTTP, // 5. 인증 방식 (HTTP)
    scheme = "bearer", // 6. 스킴 (Bearer 토큰)
    bearerFormat = "JWT" // 7. 토큰 형식
)
class SwaggerConfig