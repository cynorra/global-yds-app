package com.vocabmaster.app.domain.repository

import com.vocabmaster.app.domain.models.GrammarTopic

interface GrammarRepository {
    suspend fun getTopics(): List<GrammarTopic>
}
