package org.llmetter.domain.diary

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.llmetter.domain.emotion.EmotionAnalysis
import org.llmetter.domain.user.User
import java.time.LocalDateTime

@Entity
@Table(name = "diary_entries")
class DiaryEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "original_text", columnDefinition = "TEXT")
    var originalText: String? = null,

    @Column(name = "edited_text", columnDefinition = "TEXT")
    var editedText: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    var status: DiaryStatus = DiaryStatus.PROCESSING,

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @OneToOne(mappedBy = "diaryEntry", cascade = [CascadeType.ALL], orphanRemoval = true)
    var voiceRecording: VoiceRecording? = null

    @OneToOne(mappedBy = "diaryEntry", cascade = [CascadeType.ALL], orphanRemoval = true)
    var emotionAnalysis: EmotionAnalysis? = null

    fun updateText(newText: String) {
        this.editedText = newText
    }

    fun markAsCompleted() {
        this.status = DiaryStatus.COMPLETED
    }

    fun markAsFailed() {
        this.status = DiaryStatus.FAILED
    }
}

enum class DiaryStatus {
    PROCESSING,
    COMPLETED,
    FAILED
}
