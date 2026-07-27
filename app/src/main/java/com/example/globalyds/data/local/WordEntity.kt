package com.example.globalyds.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val id: String,
    val englishWord: String,
    val turkishMeaning: String,
    val exampleSentence: String = "",
    val isLearned: Boolean = false
)
