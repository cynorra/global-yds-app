package com.vocabmaster.app.domain.repository

import com.vocabmaster.app.domain.models.Word

interface VocabularyRepository {
    suspend fun getWordsToLearn(): List<Word>
    suspend fun getLearnedWords(): List<Word>
    suspend fun markWordAsLearned(wordId: String)
}
