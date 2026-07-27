package com.vocabmaster.app.data.repository

import com.vocabmaster.app.data.remote.TranslationApi
import com.vocabmaster.app.domain.models.Word
import com.vocabmaster.app.domain.repository.VocabularyRepository
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RealVocabularyRepositoryImpl(
    private val api: TranslationApi
) : VocabularyRepository {
    
    // A small list of common English words for our MVP testing
    private val defaultWords = listOf("Abandon", "Abstract", "Benefit", "Capable", "Dynamic")

    override suspend fun getDailyWords(): List<Word> = withContext(Dispatchers.IO) {
        val resultList = mutableListOf<Word>()
        
        // Cihazın geçerli dil kodunu al (örneğin tr, es, fr, de vb.)
        val targetLanguage = Locale.getDefault().language
        val langPair = "en|$targetLanguage"
        
        for ((index, engWord) in defaultWords.withIndex()) {
            try {
                // Fetch translation dynamically based on device language
                val response = api.translateWord(word = engWord, langPair = langPair)
                val trMeaning = response.responseData?.translatedText ?: "Bilinmiyor"
                
                resultList.add(
                    Word(
                        id = index.toString(),
                        englishWord = engWord,
                        turkishMeaning = trMeaning,
                        exampleSentence = "Example sentence for $engWord",
                        isLearned = false
                    )
                )
            } catch (e: Exception) {
                // If network fails, add a fallback
                resultList.add(
                    Word(
                        id = index.toString(),
                        englishWord = engWord,
                        turkishMeaning = "Çeviri Bulunamadı",
                        exampleSentence = "Network Error",
                        isLearned = false
                    )
                )
            }
        }
        return@withContext resultList
    }

    override suspend fun getWordsForModule(moduleId: String): List<Word> {
        return getDailyWords() // Fallback to daily words for now
    }

    override suspend fun markWordAsLearned(wordId: String) {
        // Ignored for now since Room DB is not fully wired to ViewModel in this step
    }
}
