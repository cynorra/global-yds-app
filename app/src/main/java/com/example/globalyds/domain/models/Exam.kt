package com.example.globalyds.domain.models

data class Exam(
    val id: String,
    val title: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val score: Int = 0
)
