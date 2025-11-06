package com.wafflestudio.spring2025.config

import com.wafflestudio.spring2025.user.JwtAuthenticationFilter
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import jakarta.servlet.http.HttpServletResponse // 1. (추가) import
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    companion object {
        val PERMITTED_PATHS = listOf(
            "/",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/api/v1/auth/**"
        )
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // 2. (추가) "익명 사용자" 인증을 비활성화합니다.
            .anonymous { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(*PERMITTED_PATHS.toTypedArray()).permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/lectures/fetch").permitAll()
                    .anyRequest().authenticated()
            }
            // 3. (추가) 인증 실패(401) 시 동작을 명시적으로 정의합니다.
            .exceptionHandling {
                it.authenticationEntryPoint { request, response, authException ->
                    // 401 Unauthorized 응답을 반환합니다.
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                }
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}