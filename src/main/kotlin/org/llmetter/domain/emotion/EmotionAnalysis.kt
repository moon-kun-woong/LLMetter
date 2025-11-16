package org.llmetter.domain.emotion

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.llmetter.domain.diary.DiaryEntry
import java.time.LocalDateTime

@Entity
@Table(name = "emotion_analyses")
class EmotionAnalysis(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_entry_id", nullable = false, unique = true)
    val diaryEntry: DiaryEntry,

    @Column(nullable = false, name = "emotion_score")
    val emotionScore: Short, // -20 ~ +20

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "primary_emotion", length = 50)
    val primaryEmotion: EmotionCategory,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, name = "emotion_distribution", columnDefinition = "jsonb")
    val emotionDistribution: Map<String, Double>,

    @Column(name = "keywords", columnDefinition = "TEXT[]")
    val keywords: Array<String>? = null,

    @Column(name = "summary", columnDefinition = "TEXT")
    val summary: String? = null,

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(emotionScore in -20..20) {
            "Emotion score must be between -20 and 20, but was $emotionScore"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmotionAnalysis) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
