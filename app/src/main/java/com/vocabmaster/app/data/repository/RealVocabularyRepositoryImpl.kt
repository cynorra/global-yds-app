package com.vocabmaster.app.data.repository

import android.content.Context
import com.vocabmaster.app.R
import com.vocabmaster.app.data.local.WordDao
import com.vocabmaster.app.data.local.WordEntity
import com.vocabmaster.app.data.remote.TranslationApi
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.Word
import com.vocabmaster.app.domain.repository.VocabularyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Word bank shape as stored in the `word_bank` Firestore collection - the single source of
 * truth for which words exist in the app. Editable directly from the Firestore console
 * (add/edit/remove documents there); no app release is needed to change the word list.
 */
private data class MasterWord(
    val id: String = "",
    val englishWord: String = "",
    val exampleSentence: String = "",
    val targetExam: String = "ALL"
)

class RealVocabularyRepositoryImpl(
    private val api: TranslationApi,
    private val wordDao: WordDao,
    private val firestore: FirebaseFirestore,
    context: Context
) : VocabularyRepository {

    private val appContext = context.applicationContext

    // One-time seed for the shared Firestore word bank, used only the very first time the
    // `word_bank` collection is empty (e.g. a brand new Firebase project). After that, the
    // bank lives entirely in Firestore and this list is never read again.
    // Curated pre-bundled vocabulary bank across all 10 exam categories with Turkish meanings
    // and context sentences. Ensures the user always has rich content offline and on first launch
    // with 0ms loading time.
    private val preBundledWords: List<WordEntity> by lazy {
        com.vocabmaster.app.data.local.AssetDataLoader.loadWords(appContext)
    }

    private val initialSeedWords by lazy {
        preBundledWords.map { it.englishWord to it.exampleSentence }
    }
    override suspend fun getWordsToLearn(): Resource<List<Word>> = withContext(Dispatchers.IO) {
        try {
            ensureWordsCached()
            val localWords = wordDao.getAllWords().filter { !it.isLearned }.map { it.toDomain() }
            if (localWords.isEmpty()) {
                // If local DB is somehow still empty, seed immediately from preBundledWords
                wordDao.insertWords(preBundledWords)
                Resource.Success(preBundledWords.map { it.toDomain() })
            } else {
                Resource.Success(localWords)
            }
        } catch (e: Exception) {
            Resource.Success(preBundledWords.map { it.toDomain() })
        }
    }

    override suspend fun getLearnedWords(): Resource<List<Word>> = withContext(Dispatchers.IO) {
        try {
            ensureWordsCached()
            Resource.Success(wordDao.getAllWords().filter { it.isLearned }.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Success(emptyList())
        }
    }

    override suspend fun markWordAsLearned(wordId: String): Unit = withContext(Dispatchers.IO) {
        wordDao.markAsLearned(wordId)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            GamificationRepository.recordDailyActivity(firestore, uid)
        }
    }

    override suspend fun deleteWord(wordId: String) = withContext(Dispatchers.IO) {
        wordDao.deleteWord(wordId)
    }

    // Firestore's `word_bank` collection is the single source of truth for which words exist.
    // This only ever downloads words the device doesn't already have locally, so an admin can
    // add new words in Firestore at any time and every device will pick up just the delta on
    // its next visit to Vocabulary - no re-download of the whole bank, no app update needed.
    private suspend fun ensureWordsCached() {
        // Instant offline hydration: populate Room with pre-bundled words immediately
        val currentWords = wordDao.getAllWords()
        if (currentWords.isEmpty()) {
            wordDao.insertWords(preBundledWords)
        } else if (currentWords.size < preBundledWords.size) {
            val existingIds = currentWords.map { it.id }.toSet()
            val missing = preBundledWords.filterNot { it.id in existingIds }
            if (missing.isNotEmpty()) {
                wordDao.insertWords(missing)
            }
        }

        val masterWords = try {
            loadOrSeedMasterBank()
        } catch (e: Exception) {
            emptyList()
        }
        if (masterWords.isEmpty()) return

        val existingIds = wordDao.getAllWords().map { it.id }.toSet()
        val missingWords = masterWords.filterNot { it.id in existingIds }
        if (missingWords.isEmpty()) return

        val targetLanguage = Locale.getDefault().language
        val translationCache = firestore.collection("words").document(targetLanguage).collection("items")

        val alreadyTranslated = try {
            translationCache.get().await().documents
                .mapNotNull { it.toObject(WordEntity::class.java) }
                .associateBy { it.id }
        } catch (e: Exception) {
            emptyMap()
        }

        val needTranslation = missingWords.filterNot { it.id in alreadyTranslated.keys }
        val newlyTranslated = if (needTranslation.isNotEmpty()) {
            try {
                translateAndCache(targetLanguage, needTranslation, translationCache)
            } catch (e: Exception) {
                emptyMap()
            }
        } else {
            emptyMap()
        }

        val newWords = missingWords.mapNotNull { masterWord ->
            val entity = alreadyTranslated[masterWord.id]
                ?: newlyTranslated[masterWord.id]
                ?: preBundledWords.firstOrNull { it.englishWord.equals(masterWord.englishWord, ignoreCase = true) }
                ?: return@mapNotNull null

            if (entity.targetExam.isBlank() || entity.targetExam == "ALL") {
                entity.copy(targetExam = masterWord.targetExam)
            } else {
                entity
            }
        }
        if (newWords.isNotEmpty()) {
            wordDao.insertWords(newWords)
        }
    }

    private suspend fun loadOrSeedMasterBank(): List<MasterWord> {
        val masterCollection = firestore.collection(MASTER_COLLECTION)
        return try {
            val existing = masterCollection.get().await().documents.mapNotNull { doc ->
                val word = doc.toObject(MasterWord::class.java) ?: return@mapNotNull null
                val exam = if (word.targetExam.isBlank() || word.targetExam == "ALL") {
                    when {
                        word.englishWord.equals("Acquisition", true) || word.englishWord.equals("Compliance", true) ||
                        word.englishWord.equals("Feasibility", true) || word.englishWord.equals("Remittance", true) ||
                        word.englishWord.equals("Quarterly", true) || word.englishWord.equals("Benefit", true) ||
                        word.englishWord.equals("Decline", true) -> "TOEIC"

                        word.englishWord.equals("Corroborate", true) || word.englishWord.equals("Depletion", true) ||
                        word.englishWord.equals("Discrepancy", true) || word.englishWord.equals("Ambiguous", true) ||
                        word.englishWord.equals("Anticipate", true) || word.englishWord.equals("Crucial", true) -> "IELTS"

                        word.englishWord.equals("Ubiquitous", true) || word.englishWord.equals("Tenacious", true) ||
                        word.englishWord.equals("Hypothesis", true) || word.englishWord.equals("Dormant", true) ||
                        word.englishWord.equals("Indigenous", true) || word.englishWord.equals("Fundamental", true) -> "TOEFL"

                        word.englishWord.equals("Taciturn", true) || word.englishWord.equals("Obfuscate", true) ||
                        word.englishWord.equals("Ephemeral", true) || word.englishWord.equals("Impeccable", true) ||
                        word.englishWord.equals("Gregarious", true) || word.englishWord.equals("Vulnerable", true) -> "GRE"

                        word.englishWord.equals("Contradict", true) || word.englishWord.equals("Demonstrate", true) ||
                        word.englishWord.equals("Evaluate", true) || word.englishWord.equals("Significant", true) ||
                        word.englishWord.equals("Justify", true) -> "SAT"

                        word.englishWord.equals("Capable", true) || word.englishWord.equals("Enhance", true) ||
                        word.englishWord.equals("Sustain", true) || word.englishWord.equals("Persist", true) ||
                        word.englishWord.equals("Reluctant", true) -> "CAMBRIDGE"

                        word.englishWord.equals("Adequate", true) || word.englishWord.equals("Considerable", true) ||
                        word.englishWord.equals("Predict", true) || word.englishWord.equals("Dynamic", true) -> "PTE"

                        word.englishWord.equals("Abandon", true) || word.englishWord.equals("Abstract", true) ||
                        word.englishWord.equals("Accurate", true) || word.englishWord.equals("Achieve", true) ||
                        word.englishWord.equals("Adjust", true) -> "DET"

                        word.englishWord.equals("Gradual", true) || word.englishWord.equals("Maintain", true) ||
                        word.englishWord.equals("Neglect", true) || word.englishWord.equals("Overcome", true) -> "EIKEN"

                        else -> "YDS"
                    }
                } else {
                    word.targetExam
                }
                word.copy(targetExam = exam)
            }
            existing.ifEmpty { seedMasterBank(masterCollection) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Runs once, only if the shared word bank has never been populated (e.g. a fresh Firebase
    // project). Writes the starter word list so the app is usable out of the box; from then on
    // the bank is managed entirely in Firestore, not in this code.
    private suspend fun seedMasterBank(masterCollection: CollectionReference): List<MasterWord> {
        val seed = initialSeedWords.mapIndexed { index, (word, example) ->
            MasterWord(id = index.toString(), englishWord = word, exampleSentence = example, targetExam = "ALL")
        }
        try {
            seed.forEach { word ->
                masterCollection.document(word.id).set(
                    mapOf("id" to word.id, "englishWord" to word.englishWord, "exampleSentence" to word.exampleSentence, "targetExam" to word.targetExam)
                ).await()
            }
        } catch (e: Exception) {
            // Offline on very first run: the app still works this session with the seed list
            // in memory; another device (or this one later) will perform the seed write.
        }
        return seed
    }

    // Translates only the words this device is missing (concurrently, so N words load in
    // roughly the time of one network round trip) and writes them to the shared per-language
    // Firestore cache so no other device ever has to translate the same word again.
    private suspend fun translateAndCache(
        targetLanguage: String,
        words: List<MasterWord>,
        translationCache: CollectionReference
    ): Map<String, WordEntity> {
        val langPair = "en|$targetLanguage"

        val translated = coroutineScope {
            words.map { masterWord ->
                async {
                    val meaning = if (targetLanguage == "en") {
                        masterWord.englishWord
                    } else {
                        try {
                            api.translateWord(word = masterWord.englishWord, langPair = langPair)
                                .responseData?.translatedText
                                ?: appContext.getString(R.string.vocabulary_meaning_unknown)
                        } catch (e: Exception) {
                            appContext.getString(R.string.vocabulary_translation_not_found)
                        }
                    }
                    WordEntity(
                        id = masterWord.id,
                        englishWord = masterWord.englishWord,
                        turkishMeaning = meaning,
                        exampleSentence = masterWord.exampleSentence,
                        isLearned = false,
                        targetExam = masterWord.targetExam
                    )
                }
            }.awaitAll()
        }

        try {
            translated.forEach { word ->
                translationCache.document(word.id).set(
                    mapOf(
                        "id" to word.id,
                        "englishWord" to word.englishWord,
                        "turkishMeaning" to word.turkishMeaning,
                        "exampleSentence" to word.exampleSentence,
                        "targetExam" to word.targetExam
                    )
                ).await()
            }
        } catch (e: Exception) {
            // Firestore write failed (e.g. offline); words are still usable locally this
            // session, another device will simply cache them later.
        }

        return translated.associateBy { it.id }
    }

    private fun WordEntity.toDomain() = Word(
        id = id,
        englishWord = englishWord,
        turkishMeaning = turkishMeaning,
        exampleSentence = exampleSentence,
        isLearned = isLearned,
        targetExam = targetExam
    )

    companion object {
        private const val MASTER_COLLECTION = "word_bank"
    }
}
