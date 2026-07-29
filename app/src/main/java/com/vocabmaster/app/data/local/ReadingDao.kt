package com.vocabmaster.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReadingDao {
    @Query("SELECT * FROM reading_passages")
    suspend fun getAllPassages(): List<ReadingPassageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassages(passages: List<ReadingPassageEntity>)
}
