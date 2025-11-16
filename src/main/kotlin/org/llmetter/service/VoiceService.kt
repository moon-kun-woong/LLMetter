package org.llmetter.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.llmetter.domain.diary.DiaryEntry
import org.llmetter.domain.diary.DiaryRepository
import org.llmetter.domain.diary.DiaryStatus
import org.llmetter.domain.diary.VoiceRecording
import org.llmetter.domain.diary.VoiceRecordingRepository
import org.llmetter.domain.user.UserRepository
import org.llmetter.util.EncryptionUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
@Transactional
class VoiceService(
    private val diaryRepository: DiaryRepository,
    private val voiceRecordingRepository: VoiceRecordingRepository,
    private val userRepository: UserRepository,
    private val encryptionUtil: EncryptionUtil,
    private val sttService: STTService,
    private val emotionAnalysisService: EmotionAnalysisService,
    @Value("\${app.voice.max-file-size-bytes}")
    private val maxFileSize: Long,
    @Value("\${app.voice.max-duration-seconds}")
    private val maxDuration: Int
) {

    suspend fun uploadVoice(userId: Long, audioFile: MultipartFile): DiaryEntry {
        // 파일 검증
        validateAudioFile(audioFile)

        // 사용자 확인
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다") }

        // DiaryEntry 생성 (PROCESSING 상태)
        val diaryEntry = DiaryEntry(
            user = user,
            status = DiaryStatus.PROCESSING
        )
        val savedDiaryEntry = diaryRepository.save(diaryEntry)

        // 음성 파일 암호화 및 저장
        val audioData = audioFile.bytes
        val encryptedData = encryptionUtil.encrypt(audioData)

        val voiceRecording = VoiceRecording(
            diaryEntry = savedDiaryEntry,
            encryptedData = encryptedData.data,
            encryptionIv = encryptedData.iv,
            fileFormat = getFileFormat(audioFile.contentType),
            fileSize = audioFile.size,
            durationSeconds = 0 // 실제로는 오디오 파일 분석 필요
        )
        voiceRecordingRepository.save(voiceRecording)

        // 비동기로 STT 및 감정 분석 처리
        CoroutineScope(Dispatchers.IO).launch {
            processVoiceToText(savedDiaryEntry.id!!, encryptedData)
        }

        return savedDiaryEntry
    }

    private suspend fun processVoiceToText(diaryEntryId: Long, encryptedData: EncryptionUtil.EncryptedData) {
        try {
            // 복호화
            val audioData = encryptionUtil.decrypt(encryptedData.data, encryptedData.iv)

            // STT 처리
            val transcribedText = sttService.transcribe(audioData)

            // DiaryEntry 업데이트
            val diaryEntry = diaryRepository.findById(diaryEntryId).orElseThrow()
            diaryEntry.originalText = transcribedText
            diaryEntry.editedText = transcribedText
            diaryRepository.save(diaryEntry)

            // 감정 분석 처리
            emotionAnalysisService.analyzeEmotion(diaryEntry)

            // 상태를 COMPLETED로 변경
            diaryEntry.markAsCompleted()
            diaryRepository.save(diaryEntry)

        } catch (e: Exception) {
            // 실패 시 상태 업데이트
            val diaryEntry = diaryRepository.findById(diaryEntryId).orElseThrow()
            diaryEntry.markAsFailed()
            diaryRepository.save(diaryEntry)
            throw e
        }
    }

    fun retrySTT(diaryEntryId: Long, userId: Long) {
        val diaryEntry = diaryRepository.findById(diaryEntryId)
            .orElseThrow { IllegalArgumentException("일기를 찾을 수 없습니다") }

        if (diaryEntry.user.id != userId) {
            throw IllegalArgumentException("권한이 없습니다")
        }

        val voiceRecording = voiceRecordingRepository.findByDiaryEntryId(diaryEntryId)
            .orElseThrow { IllegalArgumentException("음성 파일을 찾을 수 없습니다") }

        // 상태를 PROCESSING으로 변경
        diaryEntry.status = DiaryStatus.PROCESSING
        diaryRepository.save(diaryEntry)

        // 비동기로 재처리
        val encryptedData = EncryptionUtil.EncryptedData(
            voiceRecording.encryptedData,
            voiceRecording.encryptionIv
        )
        CoroutineScope(Dispatchers.IO).launch {
            processVoiceToText(diaryEntryId, encryptedData)
        }
    }

    private fun validateAudioFile(file: MultipartFile) {
        if (file.isEmpty) {
            throw IllegalArgumentException("파일이 비어있습니다")
        }

        if (file.size > maxFileSize) {
            throw IllegalArgumentException("파일 크기가 최대 크기($maxFileSize bytes)를 초과했습니다")
        }

        val contentType = file.contentType ?: throw IllegalArgumentException("파일 타입을 확인할 수 없습니다")
        if (!isValidAudioFormat(contentType)) {
            throw IllegalArgumentException("지원하지 않는 파일 형식입니다. (지원: webm, mp3, wav)")
        }
    }

    private fun isValidAudioFormat(contentType: String): Boolean {
        return contentType in listOf(
            "audio/webm",
            "audio/mpeg",
            "audio/mp3",
            "audio/wav",
            "audio/wave",
            "audio/x-wav"
        )
    }

    private fun getFileFormat(contentType: String?): String {
        return when (contentType) {
            "audio/webm" -> "webm"
            "audio/mpeg", "audio/mp3" -> "mp3"
            "audio/wav", "audio/wave", "audio/x-wav" -> "wav"
            else -> "unknown"
        }
    }
}
