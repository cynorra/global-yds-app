package com.vocabmaster.app.data.repository

import com.vocabmaster.app.domain.models.Word
import com.vocabmaster.app.domain.repository.VocabularyRepository
import kotlinx.coroutines.delay

class MockVocabularyRepositoryImpl : VocabularyRepository {
    private val allWords = mutableListOf(
        Word("1", "Abandon", "Terk etmek", "They abandoned the stolen car.", false),
        Word("2", "Abstract", "Soyut", "Abstract art is difficult to understand.", false),
        Word("3", "Accurate", "Kesin, Doğru", "The figures are accurate.", false),
        Word("4", "Acquire", "Elde etmek", "He acquired a new skill.", false),
        Word("5", "Adequate", "Yeterli", "The supply is adequate for our needs.", false)
    )

    override suspend fun getWordsToLearn(): List<Word> {
        delay(500)
        return allWords.filter { !it.isLearned }
    }

    override suspend fun getLearnedWords(): List<Word> {
        delay(500)
        return allWords.filter { it.isLearned }
    }

    override suspend fun markWordAsLearned(wordId: String) {
        delay(300)
        val index = allWords.indexOfFirst { it.id == wordId }
        if (index != -1) {
            allWords[index] = allWords[index].copy(isLearned = true)
        }
    }
}
