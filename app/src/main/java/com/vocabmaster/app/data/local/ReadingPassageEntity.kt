package com.vocabmaster.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_passages")
data class ReadingPassageEntity(
    @PrimaryKey val id: String,
    val title: String,
    val bodyText: String
)
