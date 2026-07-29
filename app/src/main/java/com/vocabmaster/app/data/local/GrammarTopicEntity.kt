package com.vocabmaster.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grammar_topics")
data class GrammarTopicEntity(
    @PrimaryKey val id: String,
    val title: String,
    val explanation: String,
    val exampleSentence: String = ""
)
