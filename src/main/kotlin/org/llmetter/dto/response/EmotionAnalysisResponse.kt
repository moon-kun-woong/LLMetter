package org.llmetter.dto.response

import org.llmetter.domain.emotion.EmotionCategory
import java.time.LocalDateTime

data class EmotionAnalysisResponse(
    val id: Long,
    val emotionScore: Short,
    val primaryEmotion: EmotionCategory,
    val emotionDistribution: Map<String, Double>,
    val keywords: List<String>,
    val summary: String?,
    val createdAt: LocalDateTime
)
