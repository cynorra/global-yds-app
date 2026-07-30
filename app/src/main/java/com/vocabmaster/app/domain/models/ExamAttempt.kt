package com.vocabmaster.app.domain.models

data class ExamAttempt(
    val examId: String = "",
    val examTitle: String = "",
    val score: Int = 0,
    val completedAt: Long = 0L
)
