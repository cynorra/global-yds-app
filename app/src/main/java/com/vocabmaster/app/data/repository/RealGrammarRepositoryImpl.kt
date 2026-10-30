package com.vocabmaster.app.data.repository

import com.vocabmaster.app.data.local.GrammarDao
import com.vocabmaster.app.data.local.GrammarTopicEntity
import com.vocabmaster.app.data.remote.TranslationApi
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.GrammarTopic
import com.vocabmaster.app.domain.repository.GrammarRepository
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
 * Grammar topic shape as stored in the `grammar_topics` Firestore collection - the single
 * source of truth for which topics exist. `explanation` is authored in Turkish; it is
 * translated per device language and cached, same as the vocabulary word bank. `title` and
 * `exampleSentence` are grammar terms/English sentences and are never translated.
 */
private data class MasterTopic(
    val id: String = "",
    val title: String = "",
    val explanation: String = "",
    val exampleSentence: String = ""
)

class RealGrammarRepositoryImpl(
    private val grammarDao: GrammarDao,
    private val api: TranslationApi,
    private val firestore: FirebaseFirestore
) : GrammarRepository {

    // One-time seed for the shared Firestore topic bank, used only the very first time the
    // `grammar_topics` collection is empty. After that the bank lives entirely in Firestore.
    private val initialSeedTopics = listOf(
        MasterTopic(
            id = "1",
            title = "Present Simple vs Present Continuous",
            explanation = "Present Simple alışkanlıkları ve genel doğruları anlatır (I work every day). Present Continuous ise şu an olan bir eylemi anlatır (I am working now).",
            exampleSentence = "She usually walks to work, but today she is driving."
        ),
        MasterTopic(
            id = "2",
            title = "Conditionals (If Clauses)",
            explanation = "Type 1 gerçek olası durumlar (If it rains, I will stay home), Type 2 hayali/olası olmayan durumlar (If I were rich, I would travel), Type 3 geçmişte gerçekleşmemiş durumlar (If I had studied, I would have passed) için kullanılır.",
            exampleSentence = "If I had known, I would have come earlier."
        ),
        MasterTopic(
            id = "3",
            title = "Passive Voice",
            explanation = "Eylemi yapan değil, eylemden etkilenen öznenin önemli olduğu cümlelerde kullanılır: be + past participle.",
            exampleSentence = "The report was written by the manager."
        ),
        MasterTopic(
            id = "4",
            title = "Relative Clauses",
            explanation = "Who, which, that, whose gibi bağlaçlarla bir ismi tanımlayan yan cümleler kurulur.",
            exampleSentence = "The book that you gave me was excellent."
        ),
        MasterTopic(
            id = "5",
            title = "Modals of Deduction",
            explanation = "Must, might, could, can't gibi modallar geçmiş veya şimdiki zamanda çıkarım yapmak için kullanılır.",
            exampleSentence = "He must have left already; his car is gone."
        ),
        MasterTopic(
            id = "6",
            title = "Present Perfect vs Past Simple",
            explanation = "Present Perfect, geçmişte başlayıp etkisi süren veya zamanı belirsiz olaylar için kullanılır (I have visited Paris). Past Simple ise zamanı belli, tamamlanmış olaylar içindir (I visited Paris in 2019).",
            exampleSentence = "I have already finished my homework, but I finished it late last night."
        ),
        MasterTopic(
            id = "7",
            title = "Present Perfect Continuous",
            explanation = "Geçmişte başlayıp hâlâ devam eden veya yakın zamanda biten, süresi vurgulanan eylemler için kullanılır: have/has been + V-ing.",
            exampleSentence = "They have been working on this project for three months."
        ),
        MasterTopic(
            id = "8",
            title = "Past Perfect",
            explanation = "Geçmişteki iki olaydan daha önce gerçekleşenini anlatmak için kullanılır: had + past participle.",
            exampleSentence = "By the time she arrived, the meeting had already started."
        ),
        MasterTopic(
            id = "9",
            title = "Future Forms (will / going to / present continuous)",
            explanation = "'Will' anlık kararlar ve tahminler, 'going to' önceden planlanmış niyetler, 'present continuous' ise ayarlanmış gelecek planları için kullanılır.",
            exampleSentence = "I'm meeting my dentist at 5 p.m. tomorrow; I think it will take an hour."
        ),
        MasterTopic(
            id = "10",
            title = "Reported Speech (Indirect Speech)",
            explanation = "Birinin söylediklerini aktarırken zamanlar genellikle bir adım geriye kayar (says → said; is → was; will → would).",
            exampleSentence = "She said that she was tired and would go home early."
        ),
        MasterTopic(
            id = "11",
            title = "Gerunds and Infinitives",
            explanation = "Bazı fiillerden sonra -ing (enjoy doing), bazılarından sonra to + fiil (decide to go) kullanılır; bu kalıplar ezbere bilinmelidir.",
            exampleSentence = "She enjoys reading novels but decided to watch a movie tonight."
        ),
        MasterTopic(
            id = "12",
            title = "Comparatives and Superlatives",
            explanation = "Kısa sıfatlarda -er/-est, uzun sıfatlarda more/most kullanılır; düzensiz biçimler (good-better-best) ayrıca öğrenilmelidir.",
            exampleSentence = "This exam is more difficult than the last one, but it's not the most difficult I've seen."
        ),
        MasterTopic(
            id = "13",
            title = "Adjective Order",
            explanation = "Birden fazla sıfat kullanıldığında sıralama genellikle şöyledir: görüş, boyut, yaş, şekil, renk, köken, malzeme (a beautiful small old round black Italian leather bag).",
            exampleSentence = "She bought a beautiful small wooden table."
        ),
        MasterTopic(
            id = "14",
            title = "Articles (a / an / the)",
            explanation = "'A/an' ilk kez bahsedilen, belirsiz tekil isimler için; 'the' hem konuşan hem dinleyenin bildiği belirli isimler için kullanılır.",
            exampleSentence = "I saw a cat in the garden; the cat was sleeping under a tree."
        ),
        MasterTopic(
            id = "15",
            title = "Question Tags",
            explanation = "Cümle olumluysa etiket olumsuz, cümle olumsuzsa etiket olumlu olur: You are coming, aren't you?",
            exampleSentence = "You haven't seen this movie before, have you?"
        ),
        MasterTopic(
            id = "16",
            title = "Wish / If Only",
            explanation = "Şimdiki zamana dair pişmanlık için wish + past simple, geçmişe dair pişmanlık için wish + past perfect kullanılır.",
            exampleSentence = "I wish I had studied harder for the exam."
        ),
        MasterTopic(
            id = "17",
            title = "Causative Form (have/get something done)",
            explanation = "Bir işi başkasına yaptırdığımızı anlatmak için kullanılır: have/get + nesne + past participle.",
            exampleSentence = "I had my car repaired last weekend."
        ),
        MasterTopic(
            id = "18",
            title = "Modals of Obligation and Permission",
            explanation = "Must/have to zorunluluk, should/ought to tavsiye, can/may izin anlamı taşır; must not ile don't have to arasındaki fark sık sorulur.",
            exampleSentence = "You must wear a seatbelt, but you don't have to drive if you're tired."
        ),
        MasterTopic(
            id = "19",
            title = "Inversion",
            explanation = "Vurgu amacıyla olumsuz zarflar (never, rarely, not only) cümle başına geldiğinde özne-yüklem sırası devrik olur.",
            exampleSentence = "Never have I seen such a beautiful sunset."
        ),
        MasterTopic(
            id = "20",
            title = "Noun Clauses",
            explanation = "That, what, whether, if gibi bağlaçlarla kurulan ve cümlede özne/nesne görevi gören yan cümlelerdir.",
            exampleSentence = "What she said surprised everyone in the room."
        )
    )

    override suspend fun getTopics(): Resource<List<GrammarTopic>> = withContext(Dispatchers.IO) {
        try {
            ensureTopicsCached()
            Resource.Success(grammarDao.getAllTopics().map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    // Same pattern as the vocabulary word bank: Firestore's `grammar_topics` collection is the
    // single source of truth. Only topics missing locally are downloaded/translated, so an
    // admin can add new topics in Firestore at any time without an app update.
    private suspend fun ensureTopicsCached() {
        val masterTopics = loadOrSeedMasterBank()
        if (masterTopics.isEmpty()) return // offline on first run with no local cache yet

        val existingIds = grammarDao.getAllTopics().map { it.id }.toSet()
        val missingTopics = masterTopics.filterNot { it.id in existingIds }
        if (missingTopics.isEmpty()) return

        val targetLanguage = Locale.getDefault().language
        val explanationCache = firestore.collection(EXPLANATION_COLLECTION).document(targetLanguage).collection("items")

        val alreadyTranslated = try {
            explanationCache.get().await().documents
                .mapNotNull { it.toObject(GrammarTopicEntity::class.java) }
                .associateBy { it.id }
        } catch (e: Exception) {
            emptyMap()
        }

        val needTranslation = missingTopics.filterNot { it.id in alreadyTranslated.keys }
        val newlyTranslated = if (needTranslation.isNotEmpty()) {
            translateAndCache(targetLanguage, needTranslation, explanationCache)
        } else {
            emptyMap()
        }

        val newTopics = missingTopics.map { alreadyTranslated[it.id] ?: newlyTranslated.getValue(it.id) }
        grammarDao.insertTopics(newTopics)
    }

    private suspend fun loadOrSeedMasterBank(): List<MasterTopic> {
        val masterCollection = firestore.collection(MASTER_COLLECTION)
        return try {
            val existing = masterCollection.get().await().documents.mapNotNull { it.toObject(MasterTopic::class.java) }
            existing.ifEmpty { seedMasterBank(masterCollection) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun seedMasterBank(masterCollection: CollectionReference): List<MasterTopic> {
        try {
            initialSeedTopics.forEach { topic ->
                masterCollection.document(topic.id).set(
                    mapOf(
                        "id" to topic.id,
                        "title" to topic.title,
                        "explanation" to topic.explanation,
                        "exampleSentence" to topic.exampleSentence
                    )
                ).await()
            }
        } catch (e: Exception) {
            // Offline on very first run: the app still works this session with the seed list
            // in memory; another device (or this one later) will perform the seed write.
        }
        return initialSeedTopics
    }

    // Translates only the topics this device is missing. The explanation is authored in
    // Turkish, so no translation call is made when the device is already set to Turkish.
    private suspend fun translateAndCache(
        targetLanguage: String,
        topics: List<MasterTopic>,
        explanationCache: CollectionReference
    ): Map<String, GrammarTopicEntity> {
        val langPair = "tr|$targetLanguage"

        val translated = coroutineScope {
            topics.map { topic ->
                async {
                    val explanation = if (targetLanguage == "tr") {
                        topic.explanation
                    } else {
                        try {
                            api.translateWord(word = topic.explanation, langPair = langPair)
                                .responseData?.translatedText
                                ?: topic.explanation
                        } catch (e: Exception) {
                            // Translation unavailable (offline/rate-limited): fall back to the
                            // Turkish source rather than showing a broken empty explanation.
                            topic.explanation
                        }
                    }
                    GrammarTopicEntity(
                        id = topic.id,
                        title = topic.title,
                        explanation = explanation,
                        exampleSentence = topic.exampleSentence
                    )
                }
            }.awaitAll()
        }

        try {
            translated.forEach { topic ->
                explanationCache.document(topic.id).set(
                    mapOf(
                        "id" to topic.id,
                        "title" to topic.title,
                        "explanation" to topic.explanation,
                        "exampleSentence" to topic.exampleSentence
                    )
                ).await()
            }
        } catch (e: Exception) {
            // Firestore write failed (e.g. offline); topics are still usable locally this
            // session, another device will simply cache them later.
        }

        return translated.associateBy { it.id }
    }

    private fun GrammarTopicEntity.toDomain() = GrammarTopic(
        id = id,
        title = title,
        explanation = explanation,
        exampleSentence = exampleSentence,
        targetExam = when (id) {
            "1" -> "🇯🇵 TOEIC & 🇹🇷 YDS"
            "2" -> "🇬🇧 IELTS & 🇺🇸 TOEFL"
            "3" -> "🇪🇺 Cambridge & 🇹🇷 YDS"
            "4" -> "🎓 SAT & 🏛️ GRE"
            "5" -> "⚡ PTE & 🦉 DET"
            else -> "🌍 Global Exams"
        }
    )

    companion object {
        private const val MASTER_COLLECTION = "grammar_topics"
        private const val EXPLANATION_COLLECTION = "grammar_explanations"
    }
}
