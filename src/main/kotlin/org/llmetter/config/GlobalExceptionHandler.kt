package org.llmetter.config

import org.llmetter.dto.response.ApiResponse
import org.llmetter.dto.response.ErrorDetail
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(ex: UsernameNotFoundException): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse(
                success = false,
                error = ErrorDetail(
                    code = "USER_NOT_FOUND",
                    message = ex.message ?: "사용자를 찾을 수 없습니다"
                )
            )
        )
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiResponse(
                success = false,
                error = ErrorDetail(
                    code = "INVALID_CREDENTIALS",
                    message = ex.message ?: "인증에 실패했습니다"
                )
            )
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit>> {
        val errors = ex.bindingResult.allErrors.associate {
            val fieldName = (it as? FieldError)?.field ?: "unknown"
            fieldName to (it.defaultMessage ?: "유효하지 않은 값입니다")
        }

        return ResponseEntity.badRequest().body(
            ApiResponse(
                success = false,
                error = ErrorDetail(
                    code = "VALIDATION_ERROR",
                    message = "입력값이 유효하지 않습니다",
                    details = errors
                )
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ApiResponse<Unit>> {
        ex.printStackTrace()
        return ResponseEntity.internalServerError().body(
            ApiResponse(
                success = false,
                error = ErrorDetail(
                    code = "INTERNAL_ERROR",
                    message = "서버 오류가 발생했습니다"
                )
            )
        )
    }
}
