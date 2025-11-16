package org.llmetter.domain.emotion

enum class EmotionCategory(
    val koreanName: String,
    val description: String
) {
    JOY("기쁨", "행복하고 즐거운 감정"),
    SADNESS("슬픔", "우울하고 슬픈 감정"),
    ANGER("분노", "화나고 짜증나는 감정"),
    ANXIETY("불안", "걱정되고 불안한 감정"),
    CALM("평온", "평화롭고 차분한 감정"),
    HOPE("희망", "기대되고 희망찬 감정"),
    LONELINESS("외로움", "고독하고 외로운 감정"),
    GRATITUDE("감사", "고맙고 감사한 감정"),
    REGRET("후회", "아쉽고 후회되는 감정"),
    EXCITEMENT("설렘", "두근거리고 신나는 감정"),
    FATIGUE("피곤함", "지치고 힘든 감정"),
    CONTENTMENT("만족", "뿌듯하고 만족스러운 감정");

    companion object {
        fun fromString(value: String): EmotionCategory? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
