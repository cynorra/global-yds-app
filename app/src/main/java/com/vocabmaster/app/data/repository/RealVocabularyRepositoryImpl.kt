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
    val exampleSentence: String = ""
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
    private val initialSeedWords = listOf(
        "Abandon" to "The sailors had to abandon the sinking ship.",
        "Abstract" to "The lecture was too abstract for most first-year students.",
        "Accurate" to "Please make sure the figures in the report are accurate.",
        "Achieve" to "She worked hard to achieve her goal of becoming a doctor.",
        "Adequate" to "The salary is barely adequate to cover living expenses.",
        "Adjust" to "It took him a while to adjust to the new time zone.",
        "Ambiguous" to "The instructions were ambiguous, so nobody knew what to do.",
        "Anticipate" to "Analysts anticipate a rise in prices next quarter.",
        "Assert" to "She firmly asserted that she was not responsible for the mistake.",
        "Benefit" to "Regular exercise can benefit both your body and your mind.",
        "Capable" to "He is more than capable of handling this project alone.",
        "Collapse" to "The old bridge collapsed under the weight of the truck.",
        "Complex" to "The negotiations turned out to be far more complex than expected.",
        "Conclude" to "The judge concluded the trial after hearing the final witness.",
        "Consequence" to "Every decision has consequences, whether good or bad.",
        "Considerable" to "The company invested a considerable amount of money in research.",
        "Contradict" to "His actions often contradict what he says in public.",
        "Convince" to "It took hours to convince her to change her mind.",
        "Crucial" to "Sleep plays a crucial role in memory and concentration.",
        "Decline" to "Sales have declined sharply since the new tax was introduced.",
        "Demonstrate" to "The teacher demonstrated how to solve the equation step by step.",
        "Deny" to "The company denied any responsibility for the accident.",
        "Dynamic" to "The city has a dynamic economy driven by technology and tourism.",
        "Emerge" to "New evidence emerged during the second week of the investigation.",
        "Enhance" to "The new software will enhance the efficiency of the whole team.",
        "Establish" to "The organization was established over fifty years ago.",
        "Evaluate" to "Teachers evaluate students' progress at the end of each term.",
        "Eventually" to "After months of training, she eventually won the championship.",
        "Evident" to "It was evident from his expression that something was wrong.",
        "Exceed" to "Demand for the product has exceeded all expectations.",
        "Fundamental" to "Trust is a fundamental part of any healthy relationship.",
        "Generate" to "The factory generates enough electricity to power the whole town.",
        "Gradual" to "There has been a gradual improvement in air quality over the years.",
        "Identify" to "Doctors were able to identify the cause of the illness quickly.",
        "Inevitable" to "Given the circumstances, the delay was probably inevitable.",
        "Interpret" to "Different readers may interpret the poem in very different ways.",
        "Justify" to "He couldn't justify spending so much money on a single item.",
        "Maintain" to "It's important to maintain a healthy balance between work and rest.",
        "Neglect" to "Parents should never neglect their children's emotional needs.",
        "Obvious" to "It was obvious to everyone that she was upset.",
        "Overcome" to "With patience and practice, she managed to overcome her fear of public speaking.",
        "Persist" to "Despite repeated failures, he continued to persist with his research.",
        "Predict" to "Nobody could have predicted how popular the app would become.",
        "Reluctant" to "She was reluctant to accept the offer without discussing it first.",
        "Reveal" to "The survey revealed that most employees wanted flexible working hours.",
        "Significant" to "There has been a significant increase in online shopping this year.",
        "Sufficient" to "We don't have sufficient evidence to draw a firm conclusion yet.",
        "Sustain" to "The economy needs steady investment to sustain long-term growth.",
        "Tremendous" to "The new policy has had a tremendous impact on small businesses.",
        "Vulnerable" to "Elderly people are particularly vulnerable during heat waves."
    )

    override suspend fun getWordsToLearn(): Resource<List<Word>> = withContext(Dispatchers.IO) {
        try {
            ensureWordsCached()
            Resource.Success(wordDao.getAllWords().filter { !it.isLearned }.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getLearnedWords(): Resource<List<Word>> = withContext(Dispatchers.IO) {
        try {
            ensureWordsCached()
            Resource.Success(wordDao.getAllWords().filter { it.isLearned }.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.message)
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
        val masterWords = loadOrSeedMasterBank()
        if (masterWords.isEmpty()) return // offline on first run with no local cache yet

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
            translateAndCache(targetLanguage, needTranslation, translationCache)
        } else {
            emptyMap()
        }

        val newWords = missingWords.map { alreadyTranslated[it.id] ?: newlyTranslated.getValue(it.id) }
        wordDao.insertWords(newWords)
    }

    private suspend fun loadOrSeedMasterBank(): List<MasterWord> {
        val masterCollection = firestore.collection(MASTER_COLLECTION)
        return try {
            val existing = masterCollection.get().await().documents.mapNotNull { it.toObject(MasterWord::class.java) }
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
            MasterWord(id = index.toString(), englishWord = word, exampleSentence = example)
        }
        try {
            seed.forEach { word ->
                masterCollection.document(word.id).set(
                    mapOf("id" to word.id, "englishWord" to word.englishWord, "exampleSentence" to word.exampleSentence)
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
                        isLearned = false
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
                        "exampleSentence" to word.exampleSentence
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
        isLearned = isLearned
    )

    companion object {
        private const val MASTER_COLLECTION = "word_bank"
    }
}
