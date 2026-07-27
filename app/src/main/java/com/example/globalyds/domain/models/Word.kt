package com.example.globalyds.domain.models

data class Word(
    val id: String,
    val englishWord: String,
    val turkishMeaning: String,
    val exampleSentence: String = "",
    val isLearned: Boolean = false
)
