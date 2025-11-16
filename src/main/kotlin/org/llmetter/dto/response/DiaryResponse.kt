package org.llmetter.dto.response

import org.llmetter.domain.diary.DiaryStatus
import java.time.LocalDateTime

data class DiaryResponse(
    val id: Long,
    val originalText: String?,
    val editedText: String?,
    val status: DiaryStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val emotionAnalysis: EmotionAnalysisResponse?
)

data class DiaryListResponse(
    val diaries: List<DiaryResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)
