package org.llmetter.dto.response

import java.time.LocalDateTime

data class EmotionGraphResponse(
    val data: List<EmotionGraphPoint>,
    val averageScore: Double?,
    val period: String
)

data class EmotionGraphPoint(
    val date: LocalDateTime,
    val emotionScore: Short,
    val primaryEmotion: String
)

data class EmotionStatisticsResponse(
    val averageScore: Double?,
    val totalEntries: Long,
    val emotionDistribution: Map<String, Int>,
    val mostFrequentEmotion: String?,
    val period: String
)
