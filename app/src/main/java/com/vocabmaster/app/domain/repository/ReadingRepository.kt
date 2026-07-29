package com.vocabmaster.app.domain.repository

import com.vocabmaster.app.domain.models.ReadingPassage

interface ReadingRepository {
    suspend fun getPassages(): List<ReadingPassage>
}
