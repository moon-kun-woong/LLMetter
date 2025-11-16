package org.llmetter.domain.diary

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface DiaryRepository : JpaRepository<DiaryEntry, Long> {
    fun findByUserId(userId: Long, pageable: Pageable): Page<DiaryEntry>

    @Query("SELECT d FROM DiaryEntry d WHERE d.user.id = :userId AND d.createdAt BETWEEN :startDate AND :endDate ORDER BY d.createdAt DESC")
    fun findByUserIdAndDateRange(
        userId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable
    ): Page<DiaryEntry>

    @Query("SELECT d FROM DiaryEntry d LEFT JOIN FETCH d.emotionAnalysis WHERE d.id = :id")
    fun findByIdWithEmotion(id: Long): Optional<DiaryEntry>

    fun countByUserIdAndStatus(userId: Long, status: DiaryStatus): Long
}
