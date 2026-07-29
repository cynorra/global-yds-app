package com.vocabmaster.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GrammarDao {
    @Query("SELECT * FROM grammar_topics")
    suspend fun getAllTopics(): List<GrammarTopicEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<GrammarTopicEntity>)
}
