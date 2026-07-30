package com.vocabmaster.app.data.repository

import com.vocabmaster.app.data.local.WordDao
import com.vocabmaster.app.data.local.WordEntity
import com.vocabmaster.app.data.remote.TranslationApi
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.Word
import com.vocabmaster.app.domain.repository.VocabularyRepository
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class RealVocabularyRepositoryImpl(
    private val api: TranslationApi,
    private val wordDao: WordDao
) : VocabularyRepository {

    // A bank of common YDS-level words, each with a hand-written example sentence. Turkish
    // meanings are fetched from the translation API and cached locally on first run.
    private val defaultWords = listOf(
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

    override suspend fun markWordAsLearned(wordId: String) = withContext(Dispatchers.IO) {
        wordDao.markAsLearned(wordId)
    }

    override suspend fun deleteWord(wordId: String) = withContext(Dispatchers.IO) {
        wordDao.deleteWord(wordId)
    }

    // Fetches translations once (concurrently, so ~50 words load in roughly the time of one
    // network round trip instead of fifty sequential ones) and caches them in Room; later calls
    // read from the local DB.
    private suspend fun ensureWordsCached() {
        if (wordDao.getAllWords().isNotEmpty()) return

        // Cihazın geçerli dil kodunu al (örneğin tr, es, fr, de vb.)
        val targetLanguage = Locale.getDefault().language
        val langPair = "en|$targetLanguage"

        val fetched = coroutineScope {
            defaultWords.mapIndexed { index, (engWord, example) ->
                async {
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
                        exampleSentence = example,
                        isLearned = false
                    )
                }
            }.awaitAll()
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
