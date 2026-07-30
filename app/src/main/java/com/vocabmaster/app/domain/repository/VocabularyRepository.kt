package com.vocabmaster.app.domain.repository

import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.Word

interface VocabularyRepository {
    suspend fun getWordsToLearn(): Resource<List<Word>>
    suspend fun getLearnedWords(): Resource<List<Word>>
    suspend fun markWordAsLearned(wordId: String)
    suspend fun deleteWord(wordId: String)
}
