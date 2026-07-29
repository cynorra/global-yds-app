package com.vocabmaster.app.domain.models

data class Question(
    val id: String = "",
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0
)
