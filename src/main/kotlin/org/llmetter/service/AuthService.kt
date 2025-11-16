package org.llmetter.service

import org.llmetter.domain.user.RefreshToken
import org.llmetter.domain.user.RefreshTokenRepository
import org.llmetter.domain.user.UserRepository
import org.llmetter.dto.request.LoginRequest
import org.llmetter.dto.response.AuthResponse
import org.llmetter.dto.response.UserInfo
import org.llmetter.util.JwtUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    @Value("\${app.jwt.access-token-expiration}")
    private val accessTokenExpiration: Long,
    @Value("\${app.jwt.refresh-token-expiration}")
    private val refreshTokenExpiration: Long
) {

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { UsernameNotFoundException("사용자를 찾을 수 없습니다") }

        // 비밀번호 검증
        if (user.passwordHash == null || !passwordEncoder.matches(request.password, user.passwordHash)) {
            throw BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다")
        }

        // 토큰 생성
        val userId = user.id!!
        val accessToken = jwtUtil.generateAccessToken(userId, user.email)
        val refreshTokenValue = jwtUtil.generateRefreshToken(userId, user.email)

        // 기존 리프레시 토큰 삭제
        refreshTokenRepository.deleteByUserId(userId)

        // 새 리프레시 토큰 저장
        val refreshToken = RefreshToken(
            user = user,
            token = refreshTokenValue,
            expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000)
        )
        refreshTokenRepository.save(refreshToken)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshTokenValue,
            expiresIn = accessTokenExpiration,
            user = UserInfo(
                id = userId,
                email = user.email,
                provider = user.provider.name
            )
        )
    }

    fun refreshAccessToken(refreshTokenValue: String): AuthResponse {
        // 리프레시 토큰 검증
        if (!jwtUtil.validateToken(refreshTokenValue)) {
            throw BadCredentialsException("유효하지 않은 리프레시 토큰입니다")
        }

        // DB에서 리프레시 토큰 조회
        val refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow { BadCredentialsException("리프레시 토큰을 찾을 수 없습니다") }

        // 만료 확인
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken)
            throw BadCredentialsException("만료된 리프레시 토큰입니다")
        }

        val user = refreshToken.user
        val userId = user.id!!

        // 새 액세스 토큰 생성
        val newAccessToken = jwtUtil.generateAccessToken(userId, user.email)

        return AuthResponse(
            accessToken = newAccessToken,
            refreshToken = refreshTokenValue,
            expiresIn = accessTokenExpiration,
            user = UserInfo(
                id = userId,
                email = user.email,
                provider = user.provider.name
            )
        )
    }

    fun logout(userId: Long) {
        refreshTokenRepository.deleteByUserId(userId)
    }
}
