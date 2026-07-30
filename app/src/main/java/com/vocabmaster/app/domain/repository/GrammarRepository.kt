package com.vocabmaster.app.domain.repository

import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.GrammarTopic

interface GrammarRepository {
    suspend fun getTopics(): Resource<List<GrammarTopic>>
}
