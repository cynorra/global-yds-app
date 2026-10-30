package com.vocabmaster.app.domain.models

data class ReadingPassage(
    val id: String,
    val title: String,
    val bodyText: String,
    val targetExam: String = "ALL",
    val levelBadge: String = "B2"
)
