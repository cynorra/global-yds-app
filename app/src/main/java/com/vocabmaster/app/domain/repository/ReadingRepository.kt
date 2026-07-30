package com.vocabmaster.app.domain.repository

import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.ReadingPassage

interface ReadingRepository {
    suspend fun getPassages(): Resource<List<ReadingPassage>>
}
