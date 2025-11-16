package org.llmetter.controller

import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.llmetter.dto.request.DiaryUpdateRequest
import org.llmetter.dto.response.ApiResponse
import org.llmetter.dto.response.DiaryListResponse
import org.llmetter.dto.response.DiaryResponse
import org.llmetter.service.DiaryService
import org.llmetter.service.VoiceService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/diaries")
class DiaryController(
    private val voiceService: VoiceService,
    private val diaryService: DiaryService
) {

    @PostMapping
    fun uploadVoice(
        @RequestParam("audio") audioFile: MultipartFile,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<DiaryResponse>> = runBlocking {
        val userId = authentication.principal as Long

        val diaryEntry = voiceService.uploadVoice(userId, audioFile)

        ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = DiaryResponse(
                    id = diaryEntry.id!!,
                    originalText = null,
                    editedText = null,
                    status = diaryEntry.status,
                    createdAt = diaryEntry.createdAt,
                    updatedAt = diaryEntry.updatedAt,
                    emotionAnalysis = null
                ),
                message = "음성 업로드가 완료되었습니다. 처리 중입니다."
            )
        )
    }

    @GetMapping
    fun getDiaries(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<DiaryListResponse>> {
        val userId = authentication.principal as Long

        val response = diaryService.getDiaries(userId, page, size, startDate, endDate)

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = response
            )
        )
    }

    @GetMapping("/{id}")
    fun getDiary(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<DiaryResponse>> {
        val userId = authentication.principal as Long

        val diary = diaryService.getDiary(id, userId)

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = diary
            )
        )
    }

    @PatchMapping("/{id}")
    fun updateDiary(
        @PathVariable id: Long,
        @Valid @RequestBody request: DiaryUpdateRequest,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<DiaryResponse>> {
        val userId = authentication.principal as Long

        val diary = diaryService.updateDiary(id, userId, request.editedText)

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = diary,
                message = "일기가 수정되었습니다"
            )
        )
    }

    @DeleteMapping("/{id}")
    fun deleteDiary(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Unit>> {
        val userId = authentication.principal as Long

        diaryService.deleteDiary(id, userId)

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "일기가 삭제되었습니다"
            )
        )
    }

    @PostMapping("/{id}/retry-stt")
    fun retrySTT(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Unit>> {
        val userId = authentication.principal as Long

        voiceService.retrySTT(id, userId)

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "STT 재처리를 시작했습니다"
            )
        )
    }
}
