package org.llmetter.domain.diary

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "voice_recordings")
class VoiceRecording(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_entry_id", nullable = false)
    val diaryEntry: DiaryEntry,

    @Column(nullable = false, name = "encrypted_data", columnDefinition = "BYTEA")
    val encryptedData: ByteArray,

    @Column(nullable = false, name = "encryption_iv")
    val encryptionIv: ByteArray,

    @Column(nullable = false, name = "file_format", length = 10)
    val fileFormat: String,

    @Column(nullable = false, name = "file_size")
    val fileSize: Long,

    @Column(nullable = false, name = "duration_seconds")
    val durationSeconds: Int,

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VoiceRecording) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
