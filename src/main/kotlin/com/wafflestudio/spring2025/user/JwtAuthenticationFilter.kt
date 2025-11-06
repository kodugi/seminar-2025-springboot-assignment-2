package com.wafflestudio.spring2025.user

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // (수정) 1. 모든 경로 검사 로직(isPermittedPath)을 제거합니다.

        try {
            val token = resolveToken(request) // 2. 토큰 추출

            if (token != null && jwtTokenProvider.validateToken(token)) { // 3. 토큰 검증
                val username = jwtTokenProvider.getUsername(token)
                val authentication = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    emptyList() // (참고) 실제로는 여기에 UserDetails나 권한을 넣어줘야 합니다.
                )
                SecurityContextHolder.getContext().authentication = authentication
                request.setAttribute("username", username)
            }
        } catch (e: Exception) {
            // (수정) 4. 토큰 검증 실패(서명 오류, 만료 등) 시,
            //         오류를 반환하는 대신 조용히 SecurityContext를 비웁니다.
            SecurityContextHolder.clearContext()
        }

        // (수정) 5. (가장 중요)
        // 인증에 성공했든(SecurityContext가 채워짐),
        // 실패했든(SecurityContext가 비어있음),
        // *항상* 다음 필터로 요청을 넘깁니다.
        filterChain.doFilter(request, response)

        // (수정) 6. "else { response.sendError(401) }" 블록을 완전히 제거했습니다.
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }
}