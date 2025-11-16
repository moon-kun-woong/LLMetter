package org.llmetter.controller

import jakarta.validation.Valid
import org.llmetter.dto.request.LoginRequest
import org.llmetter.dto.request.RefreshTokenRequest
import org.llmetter.dto.response.ApiResponse
import org.llmetter.dto.response.AuthResponse
import org.llmetter.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val response = authService.login(request)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = response,
                message = "로그인에 성공했습니다"
            )
        )
    }

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val response = authService.refreshAccessToken(request.refreshToken)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = response,
                message = "토큰이 갱신되었습니다"
            )
        )
    }

    @PostMapping("/logout")
    fun logout(authentication: Authentication): ResponseEntity<ApiResponse<Unit>> {
        val userId = authentication.principal as Long
        authService.logout(userId)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "로그아웃되었습니다"
            )
        )
    }
}
