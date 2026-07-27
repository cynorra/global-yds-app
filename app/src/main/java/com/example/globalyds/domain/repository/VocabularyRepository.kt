package com.example.globalyds.domain.repository

import com.example.globalyds.domain.models.Word

interface VocabularyRepository {
    suspend fun getWordsToLearn(): List<Word>
    suspend fun getLearnedWords(): List<Word>
    suspend fun markWordAsLearned(wordId: String)
}
