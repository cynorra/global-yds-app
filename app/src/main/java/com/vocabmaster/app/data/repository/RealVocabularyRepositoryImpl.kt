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
    private val preBundledWords = listOf(
        // TOEIC (Business & Workplace)
        WordEntity("w_toeic_1", "Acquisition", "Şirket devralma / Satın alma", "The company announced the acquisition of its rival.", false, "TOEIC"),
        WordEntity("w_toeic_2", "Compliance", "Mevzuata uyum / İtaat", "All staff members must ensure compliance with corporate security protocols.", false, "TOEIC"),
        WordEntity("w_toeic_3", "Feasibility", "Uygulanabilirlik / Fizibilite", "We conducted a feasibility study before launching the new production line.", false, "TOEIC"),
        WordEntity("w_toeic_4", "Remittance", "Para havalesi / Gönderi", "Please include the invoice number with your remittance.", false, "TOEIC"),
        WordEntity("w_toeic_5", "Quarterly", "Üç aylık / Çeyreklik", "Our quarterly financial results exceeded all market expectations.", false, "TOEIC"),
        WordEntity("w_toeic_6", "Substantial", "Önemli / Kayda değer", "The marketing campaign resulted in a substantial increase in inquiries.", false, "TOEIC"),
        WordEntity("w_toeic_7", "Vendor", "Tedarikçi / Satıcı", "We negotiated a multi-year contract with our primary hardware vendor.", false, "TOEIC"),
        WordEntity("w_toeic_8", "Merger", "Şirket birleşmesi", "The merger between the two telecom giants was approved by regulators.", false, "TOEIC"),

        // IELTS (Academic & General)
        WordEntity("w_ielts_1", "Corroborate", "Doğrulamak / Teyit etmek", "Subsequent experiments corroborated the professor's initial hypothesis.", false, "IELTS"),
        WordEntity("w_ielts_2", "Depletion", "Tükenme / Azalma", "Overfishing has caused severe depletion of coastal marine stocks.", false, "IELTS"),
        WordEntity("w_ielts_3", "Discrepancy", "Tutarsızlık / Çelişki", "The internal audit revealed a major discrepancy between the ledgers.", false, "IELTS"),
        WordEntity("w_ielts_4", "Ambiguous", "Muğlak / Belirsiz", "The treaty's ambiguous wording led to conflicting interpretations.", false, "IELTS"),
        WordEntity("w_ielts_5", "Anticipate", "Öngörmek / Ummak", "Economists anticipate a gradual stabilization in global interest rates.", false, "IELTS"),
        WordEntity("w_ielts_6", "Crucial", "Hayati / Çok önemli", "Early linguistic immersion plays a crucial role in childhood development.", false, "IELTS"),
        WordEntity("w_ielts_7", "Deteriorate", "Kötüleşmek / Bozulmak", "Air quality in metropolitan areas continues to deteriorate.", false, "IELTS"),
        WordEntity("w_ielts_8", "Proliferation", "Hızlı artış / Çoğalma", "The rapid proliferation of digital media has reshaped journalism.", false, "IELTS"),

        // TOEFL (Academic & Campus English)
        WordEntity("w_toefl_1", "Ubiquitous", "Her yerde bulunan / Çok yaygın", "Smartphones have become ubiquitous across every modern demographic.", false, "TOEFL"),
        WordEntity("w_toefl_2", "Tenacious", "Azimli / İnatçı / Tutucu", "Her tenacious defense of environmental conservation inspired millions.", false, "TOEFL"),
        WordEntity("w_toefl_3", "Hypothesis", "Varsayım / Hipotez", "The scientists designed an experiment to test their working hypothesis.", false, "TOEFL"),
        WordEntity("w_toefl_4", "Dormant", "Uykuda / Hareketsiz / Uyuyan", "The volcano had remained dormant for centuries before erupting.", false, "TOEFL"),
        WordEntity("w_toefl_5", "Indigenous", "Yerli / Yöreye özgü", "The indigenous flora of the Amazon basin possess unique medicinal qualities.", false, "TOEFL"),
        WordEntity("w_toefl_6", "Fundamental", "Temel / Esas", "Freedom of speech is a fundamental cornerstone of democratic societies.", false, "TOEFL"),
        WordEntity("w_toefl_7", "Plausible", "Akla yatkın / Makul", "He offered a plausible explanation for the sudden power failure.", false, "TOEFL"),
        WordEntity("w_toefl_8", "Cohesive", "Bağlantılı / Bütünlük arz eden", "The essay was praised for its cohesive structure and clear arguments.", false, "TOEFL"),

        // GRE / GMAT (Elite Vocabulary & Logic)
        WordEntity("w_gre_1", "Taciturn", "Az konuşan / Sessiz", "The detective was notoriously taciturn, preferring observation over speech.", false, "GRE"),
        WordEntity("w_gre_2", "Obfuscate", "Bulanıklaştırmak / Kasten karmaşıklaştırmak", "Politicians often obfuscate complex realities with simplistic slogans.", false, "GRE"),
        WordEntity("w_gre_3", "Ephemeral", "Kısa ömürlü / Geçici", "Fame on social media is notoriously ephemeral.", false, "GRE"),
        WordEntity("w_gre_4", "Impeccable", "Kusursuz / Hatasız", "Her impeccable academic credentials made her the ideal department chair.", false, "GRE"),
        WordEntity("w_gre_5", "Gregarious", "Sokulgan / Sosyal / Toplu yaşayan", "Dolphins are exceptionally gregarious animals that travel in pods.", false, "GRE"),
        WordEntity("w_gre_6", "Loquacious", "Geveze / Çok konuşkan", "The loquacious host kept guests entertained throughout the evening.", false, "GRE"),
        WordEntity("w_gre_7", "Mitigate", "Hafifletmek / Yatıştırmak", "Planting urban trees helps mitigate the heat island effect.", false, "GRE"),
        WordEntity("w_gre_8", "Anomalous", "Kural dışı / Alışılmadık", "The meteorological station recorded anomalous temperature spikes in December.", false, "GRE"),

        // SAT / ACT (Critical Reading)
        WordEntity("w_sat_1", "Contradict", "Çelişmek / Yalanlamak", "The eyewitness testimony directly contradicted the suspect's statement.", false, "SAT"),
        WordEntity("w_sat_2", "Demonstrate", "Göstermek / Kanıtlamak", "The clinical trials demonstrate the efficacy of the new vaccine.", false, "SAT"),
        WordEntity("w_sat_3", "Evaluate", "Değerlendirmek / Puanlamak", "Admissions officers evaluate applicants on both grades and leadership.", false, "SAT"),
        WordEntity("w_sat_4", "Significant", "Önemli / Anlamlı", "There was a significant improvement in test scores after tutoring.", false, "SAT"),
        WordEntity("w_sat_5", "Justify", "Haklı çıkarmak / Gerekçelendirmek", "The CFO struggled to justify the excessive expenditures.", false, "SAT"),
        WordEntity("w_sat_6", "Superfluous", "Gereksiz / Fuzuli", "Cut all superfluous adverbs to make your prose concise and impactful.", false, "SAT"),
        WordEntity("w_sat_7", "Conciliatory", "Uzlaşmacı / Yatıştırıcı", "The ambassador adopted a conciliatory tone during the peace summit.", false, "SAT"),

        // CAMBRIDGE (Use of English & Collocations)
        WordEntity("w_cam_1", "Capable", "Yetenekli / Muktedir", "She is fully capable of managing the international project team.", false, "CAMBRIDGE"),
        WordEntity("w_cam_2", "Enhance", "Geliştirmek / Artırmak", "The new software update will enhance system security and processing speed.", false, "CAMBRIDGE"),
        WordEntity("w_cam_3", "Sustain", "Sürdürmek / Ayakta tutmak", "Without constant customer feedback, it is difficult to sustain innovation.", false, "CAMBRIDGE"),
        WordEntity("w_cam_4", "Persist", "Israr etmek / Direnmek", "If the symptoms persist for more than three days, consult a physician.", false, "CAMBRIDGE"),
        WordEntity("w_cam_5", "Reluctant", "Gönülsüz / İsteksiz", "Investors were reluctant to fund unproven early-stage concepts.", false, "CAMBRIDGE"),
        WordEntity("w_cam_6", "Conscientious", "Vicdanlı / Titiz ve özenli", "Her conscientious attention to detail prevented several critical errors.", false, "CAMBRIDGE"),

        // PTE (AI Scored Academic)
        WordEntity("w_pte_1", "Adequate", "Yeterli / Kafi", "The emergency generators provided adequate lighting during the blackout.", false, "PTE"),
        WordEntity("w_pte_2", "Considerable", "Kayda değer / Hatırı sayılır", "The research institution invested considerable capital into quantum computing.", false, "PTE"),
        WordEntity("w_pte_3", "Predict", "Tahmin etmek / Öngörmek", "Meteorologists can predict severe storm trajectories with high precision.", false, "PTE"),
        WordEntity("w_pte_4", "Dynamic", "Dinamik / Hareketli", "Silicon Valley boasts a dynamic ecosystem of startups and incubators.", false, "PTE"),
        WordEntity("w_pte_5", "Reinforce", "Pekiştirmek / Güçlendirmek", "Positive reinforcement fosters a more encouraging learning environment.", false, "PTE"),

        // DET (Duolingo English Test)
        WordEntity("w_det_1", "Abandon", "Terk etmek / Bırakmak", "The crew had to abandon ship after the engine failed in rough seas.", false, "DET"),
        WordEntity("w_det_2", "Abstract", "Soyut / Kuramsal", "The lecture on philosophical ethics was too abstract for beginners.", false, "DET"),
        WordEntity("w_det_3", "Accurate", "Doğru / Kesin", "Ensure the numerical calculations in your lab report are accurate.", false, "DET"),
        WordEntity("w_det_4", "Achieve", "Başarmak / Elde etmek", "With perseverance, any diligent student can achieve their target score.", false, "DET"),
        WordEntity("w_det_5", "Adjust", "Uyum sağlamak / Ayarlamak", "It took international students a few weeks to adjust to the local climate.", false, "DET"),

        // EIKEN (Practical English)
        WordEntity("w_eiken_1", "Gradual", "Kademeli / Aşama aşama", "We observed a gradual shift toward renewable energy sources in cities.", false, "EIKEN"),
        WordEntity("w_eiken_2", "Maintain", "Sürdürmek / Korumak", "Regular maintenance helps maintain road safety throughout winter.", false, "EIKEN"),
        WordEntity("w_eiken_3", "Neglect", "İhmal etmek / Savsaklamak", "You must never neglect your sleep routine during intensive exam prep.", false, "EIKEN"),
        WordEntity("w_eiken_4", "Overcome", "Üstesinden gelmek / Aşmak", "She practiced daily to overcome her stage fright before the speech.", false, "EIKEN"),
        WordEntity("w_eiken_5", "Foster", "Teşvik etmek / Büyütmek", "Community volunteer programs foster social solidarity among neighbors.", false, "EIKEN"),

        // YDS / YÖKDİL (Akademik & Dilbilgisi)
        WordEntity("w_yds_1", "Benefit", "Yarar sağlamak / Faydalanmak", "Students benefit greatly from interactive grammar and reading drills.", false, "YDS"),
        WordEntity("w_yds_2", "Decline", "Azalmak / Reddetmek", "Oil consumption began to decline as electric vehicles gained popularity.", false, "YDS"),
        WordEntity("w_yds_3", "Vulnerable", "Hassas / Savunmasız", "Coastal infrastructure is exceptionally vulnerable to rising sea levels.", false, "YDS"),
        WordEntity("w_yds_4", "Tremendous", "Muazzam / Devasa", "The scientific breakthrough generated tremendous excitement globally.", false, "YDS"),
        WordEntity("w_yds_5", "Consequence", "Sonuç / Netice", "Unregulated industrial emissions have dire consequences for public health.", false, "YDS"),
        WordEntity("w_yds_6", "Inevitable", "Kaçınılmaz / Önlenemez", "Structural reform became inevitable following the global financial crisis.", false, "YDS"),
        WordEntity("w_yds_7", "Emerge", "Ortaya çıkmak / Belirmek", "Fascinating new archaeological insights emerged during the excavation.", false, "YDS"),
        WordEntity("w_yds_8", "Collapse", "Çökmek / Yıkılmak", "The ancient aqueduct collapsed during the magnitude 7 earthquake.", false, "YDS"),
        WordEntity("w_toeic_9", "Consolidated", "Birle�tirilmi� / Konsolide", "The CFO presented the consolidated balance sheet to shareholders.", false, "TOEIC"),
        WordEntity("w_toeic_10", "Reimbursement", "Masraf iadesi / Geri �deme", "Submit your travel receipts to claim full reimbursement.", false, "TOEIC"),
        WordEntity("w_ielts_9", "Ubiquitous", "Her yerde bulunan / Yayg�n", "Digital interfaces are now ubiquitous in modern education.", false, "IELTS"),
        WordEntity("w_ielts_10", "Mitigate", "Hafifletmek / Azaltmak", "Strict reforestation projects help mitigate carbon accumulation.", false, "IELTS"),
        WordEntity("w_toefl_9", "Conjecture", "Tahmin / Varsay�m", "Without empirical testing, the theory remains mere conjecture.", false, "TOEFL"),
        WordEntity("w_toefl_10", "Resilient", "Diren�li / �abuk toparlanan", "Certain desert succulents are remarkably resilient to prolonged drought.", false, "TOEFL"),
        WordEntity("w_gre_9", "Equivocal", "Mu�lak / �ift anlaml�", "The politician gave an equivocal response when questioned about taxes.", false, "GRE"),
        WordEntity("w_gre_10", "Alacrity", "�steklilik / Canl�l�k / �eviklik", "She accepted the research fellowship with tremendous alacrity.", false, "GRE"),
        WordEntity("w_sat_8", "Scrutinize", "Detayl�ca incelemek / Tetkik etmek", "Auditors will scrutinize every transaction over ten thousand dollars.", false, "SAT"),
        WordEntity("w_sat_9", "Vindicate", "Aklamak / Hakl�l���n� kan�tlamak", "Newly uncovered DNA evidence served to vindicate the defendant.", false, "SAT"),
        WordEntity("w_cam_7", "Reluctance", "�steksizlik / G�n�ls�zl�k", "He expressed initial reluctance but ultimately signed the partnership.", false, "CAMBRIDGE"),
        WordEntity("w_cam_8", "Disregard", "G�z ard� etmek / Yok saymak", "Do not disregard the safety warning printed on the device.", false, "CAMBRIDGE"),
        WordEntity("w_pte_6", "Substantiate", "Somut delille desteklemek / Kan�tlamak", "You must substantiate your thesis with peer-reviewed literature.", false, "PTE"),
        WordEntity("w_pte_7", "Coherent", "Tutarl� / Mant�kl�", "The speaker delivered a coherent and persuasive argument.", false, "PTE"),
        WordEntity("w_det_6", "Comprehend", "Kavramak / Tam anlamak", "It took time to comprehend the profound implications of the discovery.", false, "DET"),
        WordEntity("w_det_7", "Essential", "Temel / �ok �nemli", "Regular vocabulary revision is essential for score improvement.", false, "DET"),
        WordEntity("w_eiken_6", "Coincide", "Denk gelmek / �ak��mak", "The festival was scheduled to coincide with the cherry blossom season.", false, "EIKEN"),
        WordEntity("w_eiken_7", "Vitalize", "Canland�rmak / G��lendirmek", "New investments helped vitalize the historic shopping district.", false, "EIKEN"),
        WordEntity("w_yds_9", "Compensate", "Telafi etmek / Kar��lamak", "The government promised to compensate farmers for drought losses.", false, "YDS"),
        WordEntity("w_yds_10", "Ambiguity", "Belirsizlik / Mu�lakl�k", "Clear definitions eliminate any legal ambiguity in the contract.", false, "YDS"),
        WordEntity("w_yds_11", "Precaution", "�nlem / Tedbir", "Take every reasonable precaution when handling laboratory chemicals.", false, "YDS"),
        WordEntity("w_yds_12", "Subsequent", "Ard�ndan gelen / Sonraki", "The initial earthquake and subsequent aftershocks caused heavy damage.", false, "YDS")
    )

    private val initialSeedWords = preBundledWords.map { it.englishWord to it.exampleSentence }

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
        if (wordDao.getAllWords().isEmpty()) {
            wordDao.insertWords(preBundledWords)
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
