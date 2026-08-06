package com.vocabmaster.app.fakes

import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.GrammarTopic
import com.vocabmaster.app.domain.repository.GrammarRepository

class FakeGrammarRepository : GrammarRepository {
    var topics: Resource<List<GrammarTopic>> = Resource.Success(emptyList())
    override suspend fun getTopics(): Resource<List<GrammarTopic>> = topics
}
