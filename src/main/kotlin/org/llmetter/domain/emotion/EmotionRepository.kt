package org.llmetter.domain.emotion

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface EmotionRepository : JpaRepository<EmotionAnalysis, Long> {
    fun findByDiaryEntryId(diaryEntryId: Long): Optional<EmotionAnalysis>

    @Query("""
        SELECT e FROM EmotionAnalysis e
        WHERE e.diaryEntry.user.id = :userId
        AND e.diaryEntry.createdAt BETWEEN :startDate AND :endDate
        ORDER BY e.diaryEntry.createdAt ASC
    """)
    fun findByUserIdAndDateRange(
        userId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<EmotionAnalysis>

    @Query("""
        SELECT AVG(e.emotionScore) FROM EmotionAnalysis e
        WHERE e.diaryEntry.user.id = :userId
        AND e.diaryEntry.createdAt BETWEEN :startDate AND :endDate
    """)
    fun getAverageScoreByUserIdAndDateRange(
        userId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Double?
}
