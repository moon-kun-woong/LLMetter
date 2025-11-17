package org.llmetter.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.llmetter.domain.diary.DiaryEntry
import org.llmetter.domain.emotion.EmotionAnalysis
import org.llmetter.domain.emotion.EmotionCategory
import org.llmetter.domain.emotion.EmotionRepository
import org.springframework.ai.anthropic.AnthropicChatModel
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EmotionAnalysisService(
    private val chatModel: AnthropicChatModel,
    private val emotionRepository: EmotionRepository,
    private val objectMapper: ObjectMapper,
    private val promptLoader: org.llmetter.util.PromptLoader
) {

    suspend fun analyzeEmotion(diaryEntry: DiaryEntry): EmotionAnalysis {
        val text = diaryEntry.editedText ?: diaryEntry.originalText
            ?: throw IllegalArgumentException("분석할 텍스트가 없습니다")

        val analysisResult = callClaudeForAnalysis(text)

        val emotionAnalysis = EmotionAnalysis(
            diaryEntry = diaryEntry,
            emotionScore = analysisResult.emotionScore,
            primaryEmotion = analysisResult.primaryEmotion,
            emotionDistribution = analysisResult.emotionDistribution,
            keywords = analysisResult.keywords.toTypedArray(),
            summary = analysisResult.summary
        )

        return emotionRepository.save(emotionAnalysis)
    }

    private suspend fun callClaudeForAnalysis(text: String): EmotionAnalysisResult {
        val prompt = createAnalysisPrompt(text)

        val response = chatModel.call(Prompt(UserMessage(prompt)))
        val content = response.result.output.content

        return parseAnalysisResult(content)
    }

    private fun createAnalysisPrompt(text: String): String {
        return promptLoader.getPrompt(
            fileName = "emotion-analysis.yaml",
            key = "emotion-analysis",
            variables = mapOf("text" to text)
        )
    }

    private fun parseAnalysisResult(jsonResponse: String): EmotionAnalysisResult {
        try {
            // JSON 부분만 추출 (Claude가 추가 텍스트를 포함할 수 있음)
            val jsonStart = jsonResponse.indexOf("{")
            val jsonEnd = jsonResponse.lastIndexOf("}") + 1
            val jsonOnly = jsonResponse.substring(jsonStart, jsonEnd)

            val result: Map<String, Any> = objectMapper.readValue(jsonOnly)

            val emotionScore = (result["emotionScore"] as Number).toShort()
            val primaryEmotionStr = result["primaryEmotion"] as String
            val primaryEmotion = EmotionCategory.fromString(primaryEmotionStr)
                ?: throw IllegalArgumentException("유효하지 않은 감정 카테고리: $primaryEmotionStr")

            @Suppress("UNCHECKED_CAST")
            val emotionDistribution = (result["emotionDistribution"] as Map<String, Any>)
                .mapValues { (it.value as Number).toDouble() }

            @Suppress("UNCHECKED_CAST")
            val keywords = result["keywords"] as List<String>
            val summary = result["summary"] as String

            return EmotionAnalysisResult(
                emotionScore = emotionScore,
                primaryEmotion = primaryEmotion,
                emotionDistribution = emotionDistribution,
                keywords = keywords,
                summary = summary
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("Claude 응답 파싱 실패: ${e.message}", e)
        }
    }

    data class EmotionAnalysisResult(
        val emotionScore: Short,
        val primaryEmotion: EmotionCategory,
        val emotionDistribution: Map<String, Double>,
        val keywords: List<String>,
        val summary: String
    )
}
