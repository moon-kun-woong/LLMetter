package org.llmetter.service

import org.llmetter.domain.emotion.EmotionRepository
import org.llmetter.dto.response.EmotionGraphPoint
import org.llmetter.dto.response.EmotionGraphResponse
import org.llmetter.dto.response.EmotionStatisticsResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class EmotionService(
    private val emotionRepository: EmotionRepository
) {

    fun getEmotionGraph(
        userId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): EmotionGraphResponse {
        val emotions = emotionRepository.findByUserIdAndDateRange(userId, startDate, endDate)

        val graphPoints = emotions.map { emotion ->
            EmotionGraphPoint(
                date = emotion.diaryEntry.createdAt,
                emotionScore = emotion.emotionScore,
                primaryEmotion = emotion.primaryEmotion.koreanName
            )
        }

        val averageScore = emotionRepository.getAverageScoreByUserIdAndDateRange(userId, startDate, endDate)

        val period = determinePeriod(startDate, endDate)

        return EmotionGraphResponse(
            data = graphPoints,
            averageScore = averageScore,
            period = period
        )
    }

    fun getEmotionStatistics(
        userId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): EmotionStatisticsResponse {
        val emotions = emotionRepository.findByUserIdAndDateRange(userId, startDate, endDate)

        val averageScore = emotionRepository.getAverageScoreByUserIdAndDateRange(userId, startDate, endDate)

        // 감정 분포 계산 (각 감정이 몇 번 나타났는지)
        val emotionDistribution = emotions
            .groupBy { it.primaryEmotion.name }
            .mapValues { it.value.size }

        // 가장 많이 나타난 감정
        val mostFrequentEmotion = emotionDistribution.maxByOrNull { it.value }?.key

        val period = determinePeriod(startDate, endDate)

        return EmotionStatisticsResponse(
            averageScore = averageScore,
            totalEntries = emotions.size.toLong(),
            emotionDistribution = emotionDistribution,
            mostFrequentEmotion = mostFrequentEmotion,
            period = period
        )
    }

    private fun determinePeriod(startDate: LocalDateTime, endDate: LocalDateTime): String {
        val days = java.time.Duration.between(startDate, endDate).toDays()

        return when {
            days <= 7 -> "이번 주"
            days <= 31 -> "이번 달"
            else -> "전체"
        }
    }
}
