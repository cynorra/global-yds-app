package com.example.globalyds.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    suspend fun getAllWords(): List<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Query("UPDATE words SET isLearned = 1 WHERE id = :wordId")
    suspend fun markAsLearned(wordId: String)
}
