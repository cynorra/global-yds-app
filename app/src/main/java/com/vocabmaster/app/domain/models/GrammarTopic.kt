package com.vocabmaster.app.domain.models

data class GrammarTopic(
    val id: String,
    val title: String,
    val explanation: String,
    val exampleSentence: String = "",
    val targetExam: String = "ALL EXAMS"
)
