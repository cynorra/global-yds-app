package com.vocabmaster.app.data.repository

import com.vocabmaster.app.data.local.WordDao
import com.vocabmaster.app.data.local.WordEntity
import com.vocabmaster.app.data.remote.TranslationApi
import com.vocabmaster.app.domain.models.Word
import com.vocabmaster.app.domain.repository.VocabularyRepository
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RealVocabularyRepositoryImpl(
    private val api: TranslationApi,
    private val wordDao: WordDao
) : VocabularyRepository {

    // A small list of common English words for our MVP testing
    private val defaultWords = listOf("Abandon", "Abstract", "Benefit", "Capable", "Dynamic")

    override suspend fun getWordsToLearn(): List<Word> = withContext(Dispatchers.IO) {
        ensureWordsCached()
        wordDao.getAllWords().filter { !it.isLearned }.map { it.toDomain() }
    }

    override suspend fun getLearnedWords(): List<Word> = withContext(Dispatchers.IO) {
        ensureWordsCached()
        wordDao.getAllWords().filter { it.isLearned }.map { it.toDomain() }
    }

    override suspend fun markWordAsLearned(wordId: String) = withContext(Dispatchers.IO) {
        wordDao.markAsLearned(wordId)
    }

    // Fetches translations once and caches them in Room; later calls read from the local DB.
    private suspend fun ensureWordsCached() {
        if (wordDao.getAllWords().isNotEmpty()) return

        // Cihazın geçerli dil kodunu al (örneğin tr, es, fr, de vb.)
        val targetLanguage = Locale.getDefault().language
        val langPair = "en|$targetLanguage"

        val fetched = defaultWords.mapIndexed { index, engWord ->
            val trMeaning = try {
                api.translateWord(word = engWord, langPair = langPair).responseData?.translatedText
                    ?: "Bilinmiyor"
            } catch (e: Exception) {
                // If network fails, add a fallback
                "Çeviri Bulunamadı"
            }
            WordEntity(
                id = index.toString(),
                englishWord = engWord,
                turkishMeaning = trMeaning,
                exampleSentence = "Example sentence for $engWord",
                isLearned = false
            )
        }
        wordDao.insertWords(fetched)
    }

    private fun WordEntity.toDomain() = Word(
        id = id,
        englishWord = englishWord,
        turkishMeaning = turkishMeaning,
        exampleSentence = exampleSentence,
        isLearned = isLearned
    )
}
