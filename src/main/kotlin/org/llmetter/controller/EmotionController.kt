package org.llmetter.controller

import org.llmetter.dto.response.ApiResponse
import org.llmetter.dto.response.EmotionGraphResponse
import org.llmetter.dto.response.EmotionStatisticsResponse
import org.llmetter.service.EmotionService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/emotions")
class EmotionController(
    private val emotionService: EmotionService
) {

    @GetMapping("/graph")
    fun getEmotionGraph(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<EmotionGraphResponse>> {
        val userId = authentication.principal as Long

        val response = emotionService.getEmotionGraph(userId, startDate, endDate)

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = response
            )
        )
    }

    @GetMapping("/statistics")
    fun getEmotionStatistics(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<EmotionStatisticsResponse>> {
        val userId = authentication.principal as Long

        val response = emotionService.getEmotionStatistics(userId, startDate, endDate)

        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = response
            )
        )
    }
}
