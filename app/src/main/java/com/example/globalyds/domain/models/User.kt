package com.example.globalyds.domain.models

data class User(
    val id: String,
    val username: String,
    val email: String,
    val wordsLearnedCount: Int = 0,
    val examsCompletedCount: Int = 0
)
