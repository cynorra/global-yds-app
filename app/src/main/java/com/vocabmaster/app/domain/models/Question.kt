package com.vocabmaster.app.domain.models

data class Question(
    val id: String = "",
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0,
    // Why the correct option is correct (and by implication, why the others aren't) - empty
    // for questions that don't have one authored yet.
    val explanation: String = ""
)
