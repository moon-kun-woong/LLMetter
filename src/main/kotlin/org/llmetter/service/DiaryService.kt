package org.llmetter.service

import org.llmetter.domain.diary.DiaryRepository
import org.llmetter.domain.diary.DiaryStatus
import org.llmetter.dto.response.DiaryListResponse
import org.llmetter.dto.response.DiaryResponse
import org.llmetter.dto.response.EmotionAnalysisResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class DiaryService(
    private val diaryRepository: DiaryRepository
) {

    fun getDiaries(
        userId: Long,
        page: Int = 0,
        size: Int = 20,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null
    ): DiaryListResponse {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))

        val diaryPage = if (startDate != null && endDate != null) {
            diaryRepository.findByUserIdAndDateRange(userId, startDate, endDate, pageable)
        } else {
            diaryRepository.findByUserId(userId, pageable)
        }

        val diaryResponses = diaryPage.content.map { diary ->
            DiaryResponse(
                id = diary.id!!,
                originalText = diary.originalText,
                editedText = diary.editedText,
                status = diary.status,
                createdAt = diary.createdAt,
                updatedAt = diary.updatedAt,
                emotionAnalysis = diary.emotionAnalysis?.let { emotion ->
                    EmotionAnalysisResponse(
                        id = emotion.id!!,
                        emotionScore = emotion.emotionScore,
                        primaryEmotion = emotion.primaryEmotion,
                        emotionDistribution = emotion.emotionDistribution,
                        keywords = emotion.keywords?.toList() ?: emptyList(),
                        summary = emotion.summary,
                        createdAt = emotion.createdAt
                    )
                }
            )
        }

        return DiaryListResponse(
            diaries = diaryResponses,
            page = diaryPage.number,
            size = diaryPage.size,
            totalElements = diaryPage.totalElements,
            totalPages = diaryPage.totalPages
        )
    }

    fun getDiary(diaryId: Long, userId: Long): DiaryResponse {
        val diary = diaryRepository.findByIdWithEmotion(diaryId)
            .orElseThrow { IllegalArgumentException("일기를 찾을 수 없습니다") }

        if (diary.user.id != userId) {
            throw IllegalArgumentException("권한이 없습니다")
        }

        return DiaryResponse(
            id = diary.id!!,
            originalText = diary.originalText,
            editedText = diary.editedText,
            status = diary.status,
            createdAt = diary.createdAt,
            updatedAt = diary.updatedAt,
            emotionAnalysis = diary.emotionAnalysis?.let { emotion ->
                EmotionAnalysisResponse(
                    id = emotion.id!!,
                    emotionScore = emotion.emotionScore,
                    primaryEmotion = emotion.primaryEmotion,
                    emotionDistribution = emotion.emotionDistribution,
                    keywords = emotion.keywords?.toList() ?: emptyList(),
                    summary = emotion.summary,
                    createdAt = emotion.createdAt
                )
            }
        )
    }

    @Transactional
    fun updateDiary(diaryId: Long, userId: Long, newText: String): DiaryResponse {
        val diary = diaryRepository.findById(diaryId)
            .orElseThrow { IllegalArgumentException("일기를 찾을 수 없습니다") }

        if (diary.user.id != userId) {
            throw IllegalArgumentException("권한이 없습니다")
        }

        diary.updateText(newText)
        diaryRepository.save(diary)

        return getDiary(diaryId, userId)
    }

    @Transactional
    fun deleteDiary(diaryId: Long, userId: Long) {
        val diary = diaryRepository.findById(diaryId)
            .orElseThrow { IllegalArgumentException("일기를 찾을 수 없습니다") }

        if (diary.user.id != userId) {
            throw IllegalArgumentException("권한이 없습니다")
        }

        diaryRepository.delete(diary)
    }
}
