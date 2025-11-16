package org.llmetter.dto.request

import jakarta.validation.constraints.NotBlank

data class DiaryUpdateRequest(
    @field:NotBlank(message = "일기 내용은 필수입니다")
    val editedText: String
)
