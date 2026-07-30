package com.vocabmaster.app.domain.models

data class Exam(
    val id: String = "",
    val title: String = "",
    val totalQuestions: Int = 0,
    val durationMinutes: Int = 0
)
