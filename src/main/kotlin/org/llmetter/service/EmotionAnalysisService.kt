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
    private val objectMapper: ObjectMapper
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
        return """
다음 일기 텍스트를 분석하여 JSON 형식으로 응답해주세요.

일기 내용:
"$text"

감정 카테고리: JOY(기쁨), SADNESS(슬픔), ANGER(분노), ANXIETY(불안), CALM(평온), HOPE(희망),
LONELINESS(외로움), GRATITUDE(감사), REGRET(후회), EXCITEMENT(설렘), FATIGUE(피곤함), CONTENTMENT(만족)

JSON 형식 (다른 텍스트 없이 JSON만 응답):
{
  "emotionScore": -20부터 +20 사이의 정수 (부정적일수록 낮은 점수),
  "primaryEmotion": "주된 감정 카테고리 (위 12개 중 하나, 대문자)",
  "emotionDistribution": {
    "EMOTION_NAME": 0.0-1.0 사이의 비율 (합이 1.0이 되도록)
  },
  "keywords": ["키워드1", "키워드2", "키워드3"],
  "summary": "일기 내용을 한 문장으로 요약"
}
        """.trimIndent()
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
