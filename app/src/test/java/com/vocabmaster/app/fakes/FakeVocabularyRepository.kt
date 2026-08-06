package com.vocabmaster.app.fakes

import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.Word
import com.vocabmaster.app.domain.repository.VocabularyRepository

/**
 * Stateful fake: markWordAsLearned/deleteWord actually mutate the backing list, so tests can
 * verify VocabularyViewModel's "mutate then reload" behavior end to end.
 */
class FakeVocabularyRepository(initialWords: List<Word> = emptyList()) : VocabularyRepository {

    private val words = initialWords.toMutableList()
    var shouldError = false

    override suspend fun getWordsToLearn(): Resource<List<Word>> {
        if (shouldError) return Resource.Error("boom")
        return Resource.Success(words.filter { !it.isLearned })
    }

    override suspend fun getLearnedWords(): Resource<List<Word>> {
        if (shouldError) return Resource.Error("boom")
        return Resource.Success(words.filter { it.isLearned })
    }

    override suspend fun markWordAsLearned(wordId: String) {
        val index = words.indexOfFirst { it.id == wordId }
        if (index >= 0) words[index] = words[index].copy(isLearned = true)
    }

    override suspend fun deleteWord(wordId: String) {
        words.removeAll { it.id == wordId }
    }
}
