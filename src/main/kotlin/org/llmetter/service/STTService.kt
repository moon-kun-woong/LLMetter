package org.llmetter.service

import org.springframework.ai.openai.OpenAiAudioTranscriptionModel
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class STTService(
    private val transcriptionModel: OpenAiAudioTranscriptionModel
) {

    suspend fun transcribe(audioData: ByteArray): String {
        try {
            val audioResource = ByteArrayResource(audioData)

            // Spring AI의 call 메서드는 Resource만 받습니다
            val response = transcriptionModel.call(audioResource)

            return response ?: throw IOException("STT 처리 실패: 응답이 비어있습니다")

        } catch (e: Exception) {
            throw IOException("STT 처리 중 오류가 발생했습니다: ${e.message}", e)
        }
    }
}
