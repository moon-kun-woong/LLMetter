package org.llmetter.domain.diary

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VoiceRecordingRepository : JpaRepository<VoiceRecording, Long> {
    fun findByDiaryEntryId(diaryEntryId: Long): Optional<VoiceRecording>
}
