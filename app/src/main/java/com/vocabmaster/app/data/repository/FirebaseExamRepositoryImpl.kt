package com.vocabmaster.app.data.repository

import com.vocabmaster.app.data.remote.TranslationApi
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.Exam
import com.vocabmaster.app.domain.models.ExamAttempt
import com.vocabmaster.app.domain.models.Question
import com.vocabmaster.app.domain.repository.ExamRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FirebaseExamRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val api: TranslationApi
) : ExamRepository {

    private val examsCollection = firestore.collection("exams")

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    private fun examAttemptsCollection(uid: String) =
        firestore.collection("users").document(uid).collection("examAttempts")

    private val fallbackExams: List<Exam>
        get() = seedExams.map { seed ->
            Exam(
                id = seed.id,
                title = seed.title,
                totalQuestions = seed.questions.size,
                durationMinutes = seed.durationMinutes,
                examType = seed.examType,
                levelBadge = seed.levelBadge,
                difficulty = seed.difficulty
            )
        }

    // Exam titles are authored in Turkish in the shared `exams` bank (see seedSampleExamsIfEmpty).
    // getAllExams() returns them translated into the device's language, cached per language in
    // Firestore so the same title is only ever translated once across all devices.
    override suspend fun getAllExams(): Resource<List<Exam>> {
        return try {
            try {
                seedSampleExamsIfEmpty()
                if (com.vocabmaster.app.data.remote.RemoteConfigManager.isDailyExamEnabled) {
                    ensureDailyExam()
                }
            } catch (e: Exception) {
                // Ignore seed/daily write failure; we still serve exams
            }

            val firestoreDocs = try {
                examsCollection.get().await().documents
            } catch (e: Exception) {
                emptyList()
            }

            val remoteExams = firestoreDocs.mapNotNull { doc ->
                val exam = doc.toObject(Exam::class.java) ?: return@mapNotNull null
                val type = if (exam.examType.isBlank() || exam.examType == "ALL") {
                    when {
                        exam.id.contains("toeic", ignoreCase = true) -> "TOEIC"
                        exam.id.contains("ielts", ignoreCase = true) -> "IELTS"
                        exam.id.contains("toefl", ignoreCase = true) -> "TOEFL"
                        exam.id.contains("cambridge", ignoreCase = true) -> "CAMBRIDGE"
                        exam.id.contains("sat", ignoreCase = true) -> "SAT"
                        exam.id.contains("gre", ignoreCase = true) -> "GRE"
                        exam.id.contains("pte", ignoreCase = true) -> "PTE"
                        exam.id.contains("det", ignoreCase = true) -> "DET"
                        exam.id.contains("eiken", ignoreCase = true) -> "EIKEN"
                        exam.id.contains("tenses") || exam.id.contains("conditionals") || exam.id.contains("passive") || exam.id.contains("relative") -> "YDS"
                        else -> "ALL"
                    }
                } else {
                    exam.examType
                }
                val badge = if (exam.levelBadge.isBlank()) "B2" else exam.levelBadge
                exam.copy(examType = type, levelBadge = badge)
            }

            val combinedExams = if (remoteExams.isEmpty()) {
                fallbackExams
            } else {
                val remoteIds = remoteExams.map { it.id }.toSet()
                remoteExams + fallbackExams.filter { it.id !in remoteIds }
            }

            // Sort so the daily exam is at the top
            val sortedExams = combinedExams.sortedByDescending { it.id.startsWith("exam-daily") }
            Resource.Success(withTranslatedTitles(sortedExams))
        } catch (e: Exception) {
            Resource.Success(fallbackExams)
        }
    }

    private suspend fun ensureDailyExam() {
        // A fixed UTC calendar day, not the device's local day: the daily exam is a single
        // Firestore document shared by every user, so it must roll over at the same real-world
        // instant for everyone rather than at each user's local midnight.
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val dateStr = dateFormat.format(Date())
        val dailyId = "exam-daily-$dateStr"
        
        val exists = examsCollection.document(dailyId).get().await().exists()
        if (exists) return

        // Pick 10 random questions from the seed bank
        val allQuestions = seedExams.flatMap { it.questions }.shuffled().take(10)
        
        examsCollection.document(dailyId).set(
            mapOf(
                "id" to dailyId,
                "title" to "Exam of the Day ($dateStr)",
                "totalQuestions" to allQuestions.size,
                "durationMinutes" to 15
            )
        ).await()

        val questionsCollection = examsCollection.document(dailyId).collection("questions")
        allQuestions.forEachIndexed { index, question ->
            // Re-assign ID to ensure uniqueness in the new subcollection
            val newQId = "q${index + 1}"
            questionsCollection.document(newQId).set(
                mapOf(
                    "id" to newQId,
                    "text" to question.text,
                    "options" to question.options,
                    "correctOptionIndex" to question.correctOptionIndex,
                    "explanation" to question.explanation
                )
            ).await()
        }
    }

    private suspend fun withTranslatedTitles(exams: List<Exam>): List<Exam> {
        val targetLanguage = Locale.getDefault().language
        if (targetLanguage == "tr" || exams.isEmpty()) return exams

        val titleCache = firestore.collection(TITLE_COLLECTION).document(targetLanguage).collection("items")
        val cached = try {
            titleCache.get().await().documents.associate { it.id to it.getString("title") }
        } catch (e: Exception) {
            emptyMap()
        }

        val missing = exams.filter { cached[it.id] == null }
        val translated = if (missing.isNotEmpty()) translateAndCacheTitles(targetLanguage, missing, titleCache) else emptyMap()

        return exams.map { exam ->
            exam.copy(title = cached[exam.id] ?: translated[exam.id] ?: exam.title)
        }
    }

    private suspend fun translateAndCacheTitles(
        targetLanguage: String,
        exams: List<Exam>,
        titleCache: com.google.firebase.firestore.CollectionReference
    ): Map<String, String> {
        val langPair = "tr|$targetLanguage"

        val translated = coroutineScope {
            exams.map { exam ->
                async {
                    val title = try {
                        api.translateWord(word = exam.title, langPair = langPair).responseData?.translatedText
                            ?: exam.title
                    } catch (e: Exception) {
                        exam.title
                    }
                    exam.id to title
                }
            }.awaitAll()
        }.toMap()

        try {
            translated.forEach { (examId, title) ->
                titleCache.document(examId).set(mapOf("title" to title)).await()
            }
        } catch (e: Exception) {
            // Firestore write failed (e.g. offline); title is still usable locally this
            // session, another device will simply cache it later.
        }

        return translated
    }

    override suspend fun getCompletedExams(): Resource<List<ExamAttempt>> {
        val uid = currentUserId ?: return Resource.Success(emptyList())
        return try {
            val attempts = examAttemptsCollection(uid).get().await()
                .documents.mapNotNull { it.toObject(ExamAttempt::class.java) }
            Resource.Success(attempts)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getExamQuestions(examId: String): List<Question> {
        if (examId == "MISTAKES") return getMistakes()
        if (examId == "MICRO") return getMicroExamQuestions()
        return try {
            val fromFirestore = examsCollection.document(examId).collection("questions").get().await()
                .documents.mapNotNull { it.toObject(Question::class.java) }
            if (fromFirestore.isNotEmpty()) {
                fromFirestore
            } else {
                seedExams.firstOrNull { it.id == examId }?.questions ?: emptyList()
            }
        } catch (e: Exception) {
            seedExams.firstOrNull { it.id == examId }?.questions ?: emptyList()
        }
    }

    override suspend fun submitExamResult(examId: String, examTitle: String, score: Int, mistakes: List<Question>) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid ?: return
        try {
            val username = user.displayName?.takeIf { it.isNotBlank() } ?: (user.email ?: "").substringBefore("@")
            
            examAttemptsCollection(uid).document(examId).set(
                mapOf(
                    "examId" to examId,
                    "examTitle" to examTitle,
                    "score" to score,
                    "completedAt" to System.currentTimeMillis()
                )
            ).await()

            firestore.collection("users").document(uid).set(
                mapOf(
                    "id" to uid,
                    "username" to username,
                    "totalScore" to FieldValue.increment(score.toLong())
                ),
                SetOptions.merge()
            ).await()

            if (mistakes.isNotEmpty()) {
                val mistakesCol = firestore.collection("users").document(uid).collection("mistakes")
                val nextReviewAt = System.currentTimeMillis() + REVIEW_INTERVAL_FIRST_MS
                mistakes.forEach { question ->
                    mistakesCol.document(question.id).set(
                        mapOf(
                            "id" to question.id,
                            "text" to question.text,
                            "options" to question.options,
                            "correctOptionIndex" to question.correctOptionIndex,
                            "explanation" to question.explanation,
                            "correctStreak" to 0,
                            "nextReviewAt" to nextReviewAt,
                            "wrongCount" to FieldValue.increment(1)
                        ),
                        SetOptions.merge()
                    ).await()
                }
            }

            GamificationRepository.recordDailyActivity(firestore, uid)
        } catch (e: Exception) {
            // Swallow network/Firestore errors; caller can retry the submission later.
            Log.w(TAG, "submitExamResult failed for exam=$examId", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Spaced-repetition mistake review: only surfaces questions whose nextReviewAt has come due
    // (legacy mistake docs written before this field existed default to 0L, i.e. immediately
    // due), soonest-due first.
    override suspend fun getMistakes(): List<Question> {
        val uid = currentUserId ?: return emptyList()
        return try {
            val now = System.currentTimeMillis()
            firestore.collection("users").document(uid).collection("mistakes")
                .get().await()
                .documents
                .filter { doc -> (doc.getLong("nextReviewAt") ?: 0L) <= now }
                .sortedBy { doc -> doc.getLong("nextReviewAt") ?: 0L }
                .mapNotNull { it.toObject(Question::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Called once per question after a Mistakes Box review session. Wrong again -> reset the
    // streak and push the next review out to REVIEW_INTERVAL_FIRST_MS. Correct -> advance the
    // streak; the first correct review reschedules for REVIEW_INTERVAL_SECOND_MS, the second
    // correct review in a row means it's mastered and gets removed from the mistake bank.
    override suspend fun recordMistakeReview(questionId: String, wasCorrect: Boolean) {
        val uid = currentUserId ?: return
        val docRef = firestore.collection("users").document(uid).collection("mistakes").document(questionId)
        try {
            if (!wasCorrect) {
                docRef.set(
                    mapOf(
                        "correctStreak" to 0,
                        "nextReviewAt" to (System.currentTimeMillis() + REVIEW_INTERVAL_FIRST_MS),
                        "wrongCount" to FieldValue.increment(1)
                    ),
                    SetOptions.merge()
                ).await()
                return
            }
            val currentStreak = (docRef.get().await().getLong("correctStreak") ?: 0L).toInt()
            val newStreak = currentStreak + 1
            if (newStreak >= MASTERY_STREAK) {
                docRef.delete().await()
            } else {
                docRef.set(
                    mapOf(
                        "correctStreak" to newStreak,
                        "nextReviewAt" to (System.currentTimeMillis() + REVIEW_INTERVAL_SECOND_MS)
                    ),
                    SetOptions.merge()
                ).await()
            }
        } catch (e: Exception) {
            Log.w(TAG, "recordMistakeReview failed for question=$questionId", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Quick-practice mode: a short, randomly sampled quiz pulled from the existing seed question
    // bank, distinct from a full themed exam. A wrong answer here still lands in the mistake
    // bank via the normal submitExamResult path, so it feeds the same spaced-repetition review.
    private fun getMicroExamQuestions(): List<Question> =
        seedExams.flatMap { it.questions }.shuffled().take(MICRO_EXAM_QUESTION_COUNT)

    // Seeds the shared exam question bank the first time the collection is empty, so the
    // exam-taking flow is demoable before a real question bank is populated. This is per-bank,
    // not per-user: unlike attempts, the bank itself is shared by everyone.
    private suspend fun seedSampleExamsIfEmpty() {
        seedExams.forEach { seedExam ->
            val docRef = examsCollection.document(seedExam.id)
            val doc = docRef.get().await()
            if (!doc.exists()) {
                docRef.set(
                    mapOf(
                        "id" to seedExam.id,
                        "title" to seedExam.title,
                        "totalQuestions" to seedExam.questions.size,
                        "durationMinutes" to seedExam.durationMinutes,
                        "examType" to seedExam.examType,
                        "levelBadge" to seedExam.levelBadge,
                        "difficulty" to seedExam.difficulty
                    )
                ).await()

                val questionsCollection = docRef.collection("questions")
                seedExam.questions.forEach { question ->
                    questionsCollection.document(question.id).set(
                        mapOf(
                            "id" to question.id,
                            "text" to question.text,
                            "options" to question.options,
                            "correctOptionIndex" to question.correctOptionIndex,
                            "explanation" to question.explanation
                        )
                    ).await()
                }
            }
        }
    }

    private data class SeedExam(
        val id: String,
        val title: String,
        val durationMinutes: Int,
        val examType: String = "ALL",
        val levelBadge: String = "B2",
        val difficulty: String = "Medium",
        val questions: List<Question>
    )

    private val seedExams = listOf(
        SeedExam(
            id = "exam-toeic-business",
            title = "TOEIC: Business & Workplace Reading",
            durationMinutes = 15,
            examType = "TOEIC",
            levelBadge = "B2",
            difficulty = "Medium",
            questions = listOf(
                Question("toeic_1", "The quarterly sales report must be submitted ______ Friday at 5:00 PM.", listOf("until", "by", "for", "on"), 1, "\"by\": belirli bir son tarihe kadar eylemin tamamlanması gerektiğini belirtir (deadline)."),
                Question("toeic_2", "Due to unexpected maintenance, the conference room will be ______ until further notice.", listOf("unavailable", "availability", "unavailably", "avail"), 0, "\"will be\" fiilinden sonra sıfat gerekir: \"unavailable\" (kullanılamaz)."),
                Question("toeic_3", "All employees are reminded to submit their travel expenses to the ______ department.", listOf("accounting", "account", "accountant", "accountable"), 0, "\"accounting department\": Muhasebe departmanı (kurumsal iş İngilizcesi terimi)."),
                Question("toeic_4", "The newly appointed CEO announced that profits have increased ______ over the past fiscal quarter.", listOf("substantially", "substantial", "substance", "substantiate"), 0, "Fiili (have increased) nitelemek için zarf gerekir: \"substantially\" (önemli ölçüde)."),
                Question("toeic_5", "Please sign the attached non-disclosure ______ before returning it to Human Resources.", listOf("agree", "agreement", "agreeable", "agreeing"), 1, "\"agreement\": sözleşme/anlaşma (non-disclosure agreement = gizlilik sözleşmesi).")
            )
        ),
        SeedExam(
            id = "exam-ielts-academic",
            title = "IELTS Academic: Reading & Lexical Resource",
            durationMinutes = 18,
            examType = "IELTS",
            levelBadge = "C1",
            difficulty = "Hard",
            questions = listOf(
                Question("ielts_1", "The researcher's findings ______ the long-held hypothesis regarding climate fluctuations.", listOf("corroborated", "concocted", "alleviated", "dissipated"), 0, "\"corroborate\": delillerle doğrulamak, teyit etmek (IELTS Band 7-9 akademik fiil)."),
                Question("ielts_2", "Rapid urbanization has led to a noticeable ______ of natural habitats in the coastal region.", listOf("depletion", "propagation", "alleviation", "enhancement"), 0, "\"depletion\": tükenme, azalma (çevre ve akademik okuma parçalarının anahtar kelimesi)."),
                Question("ielts_3", "The study concluded that early childhood bilingualism provides significant cognitive ______.", listOf("detriments", "dividends", "discrepancies", "impediments"), 1, "\"cognitive dividends\": bilişsel faydalar/kazanımlar."),
                Question("ielts_4", "Despite initial skepticism, renewable energy sources have proven increasingly cost-______.", listOf("effective", "effect", "effectively", "effectiveness"), 0, "\"cost-effective\": maliyet açısından verimli/kârlı (IELTS Task 2 ve okuma kalıbı)."),
                Question("ielts_5", "The author implies that technological disruption is ______ in modern global trade.", listOf("inevitable", "transient", "trivial", "redundant"), 0, "\"inevitable\": kaçınılmaz (akademik argüman ve çıkarım sorusu).")
            )
        ),
        SeedExam(
            id = "exam-toefl-ibt",
            title = "TOEFL iBT: Academic Passage & Inferences",
            durationMinutes = 15,
            examType = "TOEFL",
            levelBadge = "C1",
            difficulty = "Hard",
            questions = listOf(
                Question("toefl_1", "In paragraph 2, the word \"ubiquitous\" is closest in meaning to ______.", listOf("omnipresent", "sparse", "profound", "ambiguous"), 0, "\"ubiquitous\": her yerde bulunan, yaygın (omnipresent)."),
                Question("toefl_2", "Which of the following can be inferred about geothermal energy?", listOf("It is limited to geologically active regions.", "It produces high levels of carbon emission.", "It is strictly a seasonal resource.", "It requires no underground piping."), 0, "TOEFL akademik metin çıkarım (inference) soru tipi."),
                Question("toefl_3", "The author mentions photosynthesis in lines 4-6 in order to ______.", listOf("illustrate solar energy conversion", "refute chemical synthesis", "criticize agrarian methods", "define cellular respiration"), 0, "TOEFL rhetorical purpose (yazarın amaca dönük anlatımı) sorusu."),
                Question("toefl_4", "The word \"tenacious\" most nearly denotes someone who is ______.", listOf("persistent", "hesitant", "fragile", "compliant"), 0, "\"tenacious\": azimli, ısrarcı, vazgeçmeyen (persistent)."),
                Question("toefl_5", "According to the passage, dormant seeds germinate only when soil moisture is ______.", listOf("sufficient", "scarce", "excessive", "negligible"), 0, "Biyoloji odaklı TOEFL okuma-anlama sorusu.")
            )
        ),
        SeedExam(
            id = "exam-sat-reading",
            title = "SAT / ACT: Critical Reading & Logic",
            durationMinutes = 15,
            examType = "SAT",
            levelBadge = "C1-C2",
            difficulty = "Hard",
            questions = listOf(
                Question("sat_1", "The speaker's demeanor shifted from overtly combative to subtly ______.", listOf("conciliatory", "bellicose", "dogmatic", "truculent"), 0, "\"conciliatory\": uzlaşmacı, yatıştırıcı (SAT zıtlık/bağlam sorusu)."),
                Question("sat_2", "Far from being a ______ expenditure, the infrastructure investment revitalized the municipal economy.", listOf("superfluous", "vital", "salutary", "judicious"), 0, "\"superfluous\": gereksiz, fuzuli (\"Far from being...\" yapısı)."),
                Question("sat_3", "As used in line 12, \"gravity\" most nearly means ______.", listOf("seriousness", "force of attraction", "weightlessness", "momentum"), 0, "SAT \"Words in Context\": gravity = ciddiyet/önem."),
                Question("sat_4", "The author's primary argument is best summarized as ______.", listOf("a nuanced critique of modern utilitarian ethics", "an absolute defense of traditional doctrines", "an empirical study of voter turnout", "a satirical review of contemporary art"), 0, "SAT ana fikir ve eleştirel mantık çıkarımı.")
            )
        ),
        SeedExam(
            id = "exam-gre-verbal",
            title = "GRE / GMAT: Verbal Reasoning & Elite Vocab",
            durationMinutes = 15,
            examType = "GRE",
            levelBadge = "C2",
            difficulty = "Expert",
            questions = listOf(
                Question("gre_1", "Known for his ______ nature, the professor seldom spoke more than a few sentences at faculty meetings.", listOf("taciturn", "garrulous", "loquacious", "ebullient"), 0, "\"taciturn\": sessiz, az konuşan (GRE yüksek frekanslı kelimesi)."),
                Question("gre_2", "The diplomat's ambiguous remarks were intentionally designed to ______ the ongoing negotiations.", listOf("obfuscate", "elucidate", "explicate", "reconcile"), 0, "\"obfuscate\": kasten bulanıklaştırmak, karmaşıklaştırmak."),
                Question("gre_3", "Her ______ scholarly reputation was built upon decades of meticulous archival research.", listOf("impeccable", "specious", "spurious", "tenuous"), 0, "\"impeccable\": kusursuz, lekesiz."),
                Question("gre_4", "Select the word that is an antonym of \"EPHEMERAL\":", listOf("perennial", "transient", "fugacious", "fleeting"), 0, "\"ephemeral\" = geçici; zıttı \"perennial\" = kalıcı, sürekli.")
            )
        ),
        SeedExam(
            id = "exam-cambridge-advanced",
            title = "Cambridge C1: Use of English & Collocations",
            durationMinutes = 15,
            examType = "CAMBRIDGE",
            levelBadge = "C1",
            difficulty = "Medium",
            questions = listOf(
                Question("cam_1", "It never ______ to me that she might be lying.", listOf("occurred", "happened", "struck", "entered"), 0, "\"It occurred to me\": aklıma geldi / başıma dank etti (Cambridge sabit kalıp)."),
                Question("cam_2", "The company decided to call ______ the strike after fruitful discussions.", listOf("off", "out", "up", "in"), 0, "\"call off\": iptal etmek (Phrasal verb collocation)."),
                Question("cam_3", "He had no choice ______ to accept the conditions.", listOf("but", "except", "without", "than"), 0, "\"have no choice but to...\": ...mekten başka çaresi olmamak."),
                Question("cam_4", "Under no circumstances ______ reveal this confidential password.", listOf("should you", "you should", "must you", "you must"), 0, "Inversion (devrik yapı): Under no circumstances should you...")
            )
        ),
        SeedExam(
            id = "exam-pte-academic",
            title = "PTE Academic: AI-Scored Reading & Collocations",
            durationMinutes = 12,
            examType = "PTE",
            levelBadge = "B2-C1",
            difficulty = "Medium",
            questions = listOf(
                Question("pte_1", "The institution requires all international applicants to demonstrate an ______ command of the English language.", listOf("adequate", "insufficient", "inapt", "ambiguous"), 0, "\"adequate command\": yeterli seviyede hakimiyet (PTE akademik collocation)."),
                Question("pte_2", "Recent econometric models indicate a ______ correlation between education expenditure and GDP growth.", listOf("substantial", "negligible", "fictitious", "trivial"), 0, "\"substantial correlation\": kayda değer/önemli ilişki (PTE Reading - Fill in blanks)."),
                Question("pte_3", "Scientists must ______ their theoretical models through repeatable empirical experimentation.", listOf("validate", "falsify", "obscure", "repudiate"), 0, "\"validate\": geçerliliğini kanıtlamak, doğrulamak."),
                Question("pte_4", "Global supply chains are ______ to unexpected geopolitical disruptions.", listOf("vulnerable", "invulnerable", "immune", "unyielding"), 0, "\"vulnerable to\": -e karşı savunmasız/kırılgan.")
            )
        ),
        SeedExam(
            id = "exam-det-adaptive",
            title = "Duolingo English Test: Context & Lexical Selection",
            durationMinutes = 10,
            examType = "DET",
            levelBadge = "B2",
            difficulty = "Medium",
            questions = listOf(
                Question("det_1", "Select the real English word among the options:", listOf("Feasible", "Conseptualize", "Accuratly", "Discript"), 0, "\"Feasible\": uygulanabilir, mümkün (DET gerçek kelime ayırt etme testi)."),
                Question("det_2", "The city council voted to ______ the historic theater rather than demolish it.", listOf("renovate", "disintegrate", "aggravate", "deteriorate"), 0, "\"renovate\": restore etmek, yenilemek (DET cümle tamamlama)."),
                Question("det_3", "She couldn't attend the symposium ______ she had a prior scheduling conflict.", listOf("because", "despite", "whereas", "nevertheless"), 0, "\"because\": neden-sonuç bağlacı."),
                Question("det_4", "Select the word that correctly completes the phrase: \"Take into ______\"", listOf("account", "mind", "head", "thought"), 0, "\"Take into account\": hesaba katmak, dikkate almak (Sabit deyim).")
            )
        ),
        SeedExam(
            id = "exam-eiken-grade",
            title = "EIKEN Grade Pre-1: Practical Reading & Social Issues",
            durationMinutes = 15,
            examType = "EIKEN",
            levelBadge = "B2",
            difficulty = "Medium",
            questions = listOf(
                Question("eiken_1", "In Japanese corporations, there has been a ______ shift toward merit-based promotions.", listOf("gradual", "hasty", "reckless", "chaotic"), 0, "\"gradual shift\": kademeli/yavaş değişim (EIKEN güncel toplum/iş hayatı sorusu)."),
                Question("eiken_2", "Municipalities are encouraging residents to ______ their household electricity consumption.", listOf("curb", "inflate", "multiply", "aggravate"), 0, "\"curb\": dizginlemek, sınırlamak/azaltmak (EIKEN çevre ve enerji politikası sorusu)."),
                Question("eiken_3", "The new high-speed rail line is expected to ______ tourism across rural prefectures.", listOf("boost", "hinder", "stagnate", "diminish"), 0, "\"boost\": canlandırmak, artırmak (bölgesel kalkınma sorusu)."),
                Question("eiken_4", "Many elderly citizens rely on local community centers to ______ active social connections.", listOf("maintain", "sever", "terminate", "forfeit"), 0, "\"maintain\": sürdürmek, devam ettirmek (EIKEN toplum ve yaşlanan nüfus teması).")
            )
        ),
        SeedExam(
            id = "exam-tenses",
            title = "Zamanlar (Tenses)",
            durationMinutes = 12,
            examType = "YDS",
            levelBadge = "B2",
            difficulty = "Medium",
            questions = listOf(
                Question("q1", "She ______ to the store every day.", listOf("go", "goes", "going", "gone"), 1, "Present Simple: 3. tekil şahısta (she/he/it) fiile -s/-es eki gelir. \"every day\" gibi zarflar alışkanlık/rutin bildirir."),
                Question("q2", "By the time we arrived, the movie ______.", listOf("already started", "had already started", "has already started", "already starts"), 1, "Past Perfect: geçmişte iki olaydan önce biteni anlatır. \"By the time we arrived\" (geçmiş) sırasında film çoktan başlamıştı."),
                Question("q3", "I ______ my homework since 6 p.m.", listOf("do", "did", "have been doing", "will do"), 2, "Present Perfect Continuous: \"since\" ile başlangıç noktası belirtilen ve hâlâ süren eylemler için kullanılır."),
                Question("q4", "They ______ dinner when the phone rang.", listOf("have", "had", "were having", "has"), 2, "Past Continuous: geçmişte devam eden bir eylem, \"the phone rang\" (Simple Past) gibi ani bir olayla kesilir."),
                Question("q5", "She ______ abroad twice this year.", listOf("traveled", "has traveled", "was traveling", "travels"), 1, "Present Perfect: \"this year\" henüz bitmemiş bir zaman dilimidir; \"twice\" ile deneyim/sayı bildirilir."),
                Question("q6", "Next week, I ______ my grandmother in London.", listOf("visit", "visited", "am visiting", "will have visited"), 2, "Present Continuous, önceden planlanmış gelecek eylemler için kullanılır (\"next week\" ile netleşen program)."),
                Question("q7", "He ______ for the company since 2015.", listOf("works", "worked", "has worked", "is working"), 2, "Present Perfect: \"since 2015\" başlangıç noktasından bugüne süren bir durumu anlatır."),
                Question("q8", "While she ______ dinner, the lights went out.", listOf("cooked", "was cooking", "cooks", "has cooked"), 1, "Past Continuous: \"while\" ile geçmişte devam eden bir eylem, başka bir olayla (went out) kesilir."),
                Question("q9", "The train ______ before we got to the station.", listOf("left", "has left", "had left", "leaves"), 2, "Past Perfect: iki geçmiş olaydan önce gerçekleşen (tren, istasyona varmadan önce ayrılmış)."),
                Question("q10", "Look! The bus ______.", listOf("comes", "came", "is coming", "has come"), 2, "Present Continuous: \"Look!\" ile şu anda, konuşma anında gerçekleşen bir eylem anlatılır.")
            )
        ),
        SeedExam(
            id = "exam-conditionals-modals",
            title = "Koşul Cümleleri ve Modallar",
            durationMinutes = 10,
            examType = "YDS",
            levelBadge = "B2",
            difficulty = "Medium",
            questions = listOf(
                Question("q1", "If it rains tomorrow, we ______ the picnic.", listOf("cancel", "will cancel", "would cancel", "canceled"), 1, "Type 1 Conditional (gerçek/olası gelecek koşul): if + Present Simple, ... will + fiil."),
                Question("q2", "If I were you, I ______ that job offer.", listOf("accept", "will accept", "would accept", "accepted"), 2, "Type 2 Conditional (şimdiki zamana ait hayali durum): if + Past Simple (were), ... would + fiil."),
                Question("q3", "If she had studied harder, she ______ the exam.", listOf("would pass", "will pass", "would have passed", "passes"), 2, "Type 3 Conditional (gerçekleşmemiş geçmiş koşul): if + Past Perfect, ... would have + past participle."),
                Question("q4", "You ______ smoke in this building; it's strictly forbidden.", listOf("don't have to", "must not", "may not", "shouldn't"), 1, "\"must not\": kesin yasak bildirir. \"don't have to\" ise zorunluluk yokluğu anlamına gelir, yasak değil."),
                Question("q5", "She ______ be at home now; I saw her car in the driveway.", listOf("must", "can't", "mustn't", "should"), 0, "\"must\": kanıta dayalı güçlü olasılık/çıkarım bildirir (arabasını gördüm, o yüzden evde olmalı)."),
                Question("q6", "He ______ have left already; his coat is still here.", listOf("must", "can't", "might", "should"), 1, "\"can't have + past participle\": mantıksal olarak imkansız bir geçmiş çıkarımı (mantosu burada, o yüzden gitmiş olamaz)."),
                Question("q7", "Passengers ______ fasten their seatbelts during takeoff.", listOf("might", "could", "must", "would"), 2, "\"must\": kural/zorunluluk bildiren modal fiildir."),
                Question("q8", "If only I ______ more time to prepare for the interview.", listOf("have", "had", "had had", "would have"), 2, "\"If only\" + Past Perfect: geçmişe dönük pişmanlık bildirir (\"keşke daha fazla zamanım olsaydı\").")
            )
        ),
        SeedExam(
            id = "exam-passive-reported",
            title = "Edilgen Çatı ve Bildirilen Konuşma",
            durationMinutes = 10,
            examType = "YDS",
            levelBadge = "B2",
            difficulty = "Medium",
            questions = listOf(
                Question("q1", "The report ______ by the manager yesterday.", listOf("wrote", "was written", "is written", "has written"), 1, "Simple Past Passive: was/were + past participle. Eylemi yapan (manager) \"by\" ile belirtilir."),
                Question("q2", "This bridge ______ in 1990.", listOf("built", "was built", "has built", "is building"), 1, "Simple Past Passive: geçmişte belirli bir tarihte tamamlanan edilgen eylem."),
                Question("q3", "English ______ in many countries around the world.", listOf("speaks", "is spoken", "spoke", "was speaking"), 1, "Present Simple Passive: genel geçer bir gerçeği edilgen çatıyla anlatır (is/are + past participle)."),
                Question("q4", "The letter ______ by tomorrow morning.", listOf("will send", "will be sent", "sends", "sent"), 1, "Future Passive: will be + past participle."),
                Question("q5", "She said that she ______ tired.", listOf("is", "was", "has been", "will be"), 1, "Reported Speech: aktarılan cümlede Present Simple (is), Past Simple'a (was) kayar (tense backshift)."),
                Question("q6", "He told me that he ______ the movie the day before.", listOf("watches", "watched", "had watched", "has watched"), 2, "Reported Speech: Past Simple, Past Perfect'e kayar; \"the day before\" ifadesi de zaman zarfının aktarımdaki karşılığıdır."),
                Question("q7", "They said they ______ back the following week.", listOf("come", "came", "will come", "would come"), 3, "Reported Speech: \"will\", \"would\"ya kayar; \"the following week\" de \"next week\"in aktarımdaki karşılığıdır."),
                Question("q8", "The teacher asked the students ______ the exercise quietly.", listOf("do", "doing", "to do", "did"), 2, "Aktarılan rica/emir yapısı: ask/tell + kişi + to + fiil.")
            )
        ),
        SeedExam(
            id = "exam-relative-comparatives",
            title = "İlgi Cümlecikleri ve Karşılaştırmalar",
            durationMinutes = 10,
            examType = "YDS",
            levelBadge = "B2",
            difficulty = "Medium",
            questions = listOf(
                Question("q1", "The man ______ car was stolen called the police.", listOf("who", "whose", "which", "that"), 1, "\"whose\": iyelik bildiren ilgi zamiri (adamın arabası -> whose car)."),
                Question("q2", "This is the restaurant ______ we had dinner last night.", listOf("which", "who", "where", "whose"), 2, "\"where\": yer bildiren bir ismin ardından gelen ilgi zamiri."),
                Question("q3", "The book, ______ was written by a famous author, became a bestseller.", listOf("that", "which", "who", "whose"), 1, "\"which\": nesneler için kullanılan ilgi zamiri; virgüllü (non-defining) yapılarda \"that\" kullanılamaz."),
                Question("q4", "She is the woman ______ helped me find my way.", listOf("whom", "who", "which", "whose"), 1, "\"who\": cümlede özne konumundaki kişi için kullanılan ilgi zamiri (\"whom\" nesne konumu için kullanılır)."),
                Question("q5", "This exam is ______ than the last one.", listOf("difficult", "more difficult", "most difficult", "the most difficult"), 1, "İki heceden uzun sıfatlarda karşılaştırma: more + sıfat + than."),
                Question("q6", "He is ______ student in the whole class.", listOf("hardworking", "more hardworking", "the most hardworking", "hardworking than"), 2, "Üstünlük derecesi (superlative): the most + sıfat, bir grup içindeki en üst düzeyi belirtir."),
                Question("q7", "This is by far ______ hotel I have ever stayed in.", listOf("good", "better", "the best", "best"), 2, "\"good\" sıfatının düzensiz üstünlük hali \"the best\"tir; \"by far\" ile pekiştirilir."),
                Question("q8", "The weather today is ______ than yesterday.", listOf("bad", "worse", "the worst", "more bad"), 1, "\"bad\" sıfatının düzensiz karşılaştırma hali \"worse\"tür (more bad diye bir yapı yoktur).")
            )
        ),
        SeedExam(
            id = "exam-toeic-office",
            title = "TOEIC: Corporate Communications & Schedules",
            durationMinutes = 15,
            examType = "TOEIC",
            levelBadge = "B2",
            difficulty = "Medium",
            questions = listOf(
                Question("toeic_off_1", "Mr. Tanaka will be out of the office until Monday; ______, inquiries should be directed to his assistant.", listOf("meanwhile", "furthermore", "nonetheless", "whereas"), 0, "\"meanwhile\": bu esnada / bu s�re zarf�nda (i� yaz��malar�nda ge�i� zarf�)."),
                Question("toeic_off_2", "The itinerary states that our flight is scheduled to ______ at Terminal 3.", listOf("arrive", "arrival", "arriving", "arrived"), 0, "\"is scheduled to\" yap�s�ndan sonra yal�n fiil gelir: arrive."),
                Question("toeic_off_3", "Please review the updated safety procedures ______ in the employee handbook.", listOf("outlined", "outlining", "outline", "outlines"), 0, "\"outlined\": k�salt�lm�� ilgi c�mleci�i (which are outlined -> outlined)."),
                Question("toeic_off_4", "Participants who register before the deadline are ______ for a 15% discount.", listOf("eligible", "capable", "reliable", "competent"), 0, "\"eligible for\": bir hakka ya da indirime hak kazanm��, uygun."),
                Question("toeic_off_5", "The factory experienced a temporary stoppage due to a power ______ yesterday.", listOf("outage", "leakage", "storage", "drainage"), 0, "\"power outage\": elektrik kesintisi (TOEIC i� ortam� terimi).")
            )
        ),
        SeedExam(
            id = "exam-ielts-general",
            title = "IELTS General: Workplace & Social Context",
            durationMinutes = 15,
            examType = "IELTS",
            levelBadge = "B2-C1",
            difficulty = "Medium",
            questions = listOf(
                Question("ielts_gen_1", "Candidates are requested to provide proof of their ______ residential address.", listOf("current", "actual", "presently", "currently"), 0, "\"current residential address\": g�ncel ikamet adresi (IELTS General form doldurma)."),
                Question("ielts_gen_2", "The tenant agreed to give one month's ______ before vacating the property.", listOf("notice", "warning", "advice", "caution"), 0, "\"give one month's notice\": bir ay �nceden bildirimde bulunmak (kira/s�zle�me terimi)."),
                Question("ielts_gen_3", "Commuters experienced significant ______ due to signal maintenance on the central line.", listOf("delays", "deferrals", "pauses", "extensions"), 0, "\"significant delays\": �nemli gecikmeler (ula��m ve g�nl�k ya�am)."),
                Question("ielts_gen_4", "Smoking is strictly prohibited on the premises in ______ with municipal bylaws.", listOf("accordance", "agreement", "conformity", "compliance"), 0, "\"in accordance with\": -e uygun olarak / gere�ince."),
                Question("ielts_gen_5", "Staff members are expected to dress in a manner that is ______ for a corporate setting.", listOf("appropriate", "approving", "appropriately", "appropriation"), 0, "\"appropriate for\": -e uygun, m�nasip (s�fat kullan�m�).")
            )
        ),
        SeedExam(
            id = "exam-toefl-science",
            title = "TOEFL: Science & Natural History Passages",
            durationMinutes = 15,
            examType = "TOEFL",
            levelBadge = "C1",
            difficulty = "Hard",
            questions = listOf(
                Question("toefl_sci_1", "The author mentions plate tectonics in paragraph 3 in order to explain ______.", listOf("the distribution of fossil deposits across continents", "the atmospheric composition of the Paleozoic era", "the migration routes of marine mammals", "the cooling mechanisms of magma reservoirs"), 0, "TOEFL Rhetorical Purpose: yazar�n delili hangi sav� desteklemek i�in kulland���n� sorgular."),
                Question("toefl_sci_2", "The word \"tenuous\" in line 14 is closest in meaning to ______.", listOf("fragile or weak", "rigid and dense", "abundant", "permanent"), 0, "\"tenuous\": zay�f, i�reti, pamuk ipli�ine ba�l� (fragile/weak)."),
                Question("toefl_sci_3", "According to the passage, hydrothermal vents support unique ecosystems because they ______.", listOf("derive metabolic energy from chemical synthesis", "receive filtered sunlight through deep ocean layers", "remain at near-freezing temperatures year-round", "depend exclusively on photosynthetic algae"), 0, "Metne dayal� bilgi teyit (Factual Information) sorusu."),
                Question("toefl_sci_4", "What can be inferred about the species mentioned in the second paragraph?", listOf("Its evolutionary adaptations were accelerated by isolation.", "It competed directly with larger mammalian predators.", "It migrated seasonally across land bridges.", "It became extinct due to catastrophic volcanic activity."), 0, "TOEFL ��kar�m (Inference) soru tipi."),
                Question("toefl_sci_5", "The word \"proliferation\" in line 22 refers to the ______ of organisms.", listOf("rapid multiplication", "gradual extinction", "sudden dispersal", "genetic mutation"), 0, "\"proliferation\": h�zl� �o�alma, �reme (rapid multiplication).")
            )
        ),
        SeedExam(
            id = "exam-cambridge-first",
            title = "Cambridge B2 First: Phrasal Verbs & Grammar",
            durationMinutes = 15,
            examType = "CAMBRIDGE",
            levelBadge = "B2",
            difficulty = "Medium",
            questions = listOf(
                Question("cam_f_1", "I can't put ______ with this constant noise any longer.", listOf("up", "down", "off", "away"), 0, "\"put up with\": katlanmak, tahamm�l etmek (Cambridge Phrasal Verb)."),
                Question("cam_f_2", "She turned ______ the lucrative job offer because she preferred freelance work.", listOf("down", "off", "away", "over"), 0, "\"turn down\": reddetmek (turn down an offer)."),
                Question("cam_f_3", "I wish I ______ more attention when the teacher was explaining the project.", listOf("had paid", "paid", "would pay", "have paid"), 0, "Ge�mi�e dair pi�manl�k: wish + Past Perfect (had paid)."),
                Question("cam_f_4", "Hardly had we arrived ______ the presentation began.", listOf("when", "than", "that", "while"), 0, "\"Hardly... when\" devrik ve zaman kal�b�."),
                Question("cam_f_5", "He apologized for ______ late to the client presentation.", listOf("being", "to be", "be", "having"), 0, "\"apologize for + V-ing\": edattan sonra gerund (being) gelir.")
            )
        ),
        SeedExam(
            id = "exam-sat-writing",
            title = "SAT: Rhetoric, Syntax & Evidence Analysis",
            durationMinutes = 15,
            examType = "SAT",
            levelBadge = "C1-C2",
            difficulty = "Hard",
            questions = listOf(
                Question("sat_w_1", "The editor suggested that the author delete the introductory clause because it was ______.", listOf("redundant", "pivotal", "illustrative", "evocative"), 0, "\"redundant\": gereksiz yere tekrarlanan, fuzuli (SAT Concise Writing kural�)."),
                Question("sat_w_2", "Neither the primary researcher nor her colleagues ______ able to replicate the anomalous result.", listOf("were", "was", "is", "has been"), 0, "Neither...nor kural�: fiil en yak�n �zneye (colleagues -> were) uyar."),
                Question("sat_w_3", "Which choice provides the most logical transition from the preceding paragraph?", listOf("Consequently, urban planning models shifted toward transit-oriented design.", "In spite of this, classical architecture remains popular.", "On the other hand, suburban expansion ceased entirely.", "Similarly, ancient roman roads were built for military logistics."), 0, "SAT Rhetorical Transition (Ba�lam ve ge�i� mant���)."),
                Question("sat_w_4", "The novel's protagonist is portrayed as both ______ in his convictions and open to debate.", listOf("resolute", "vacillating", "dogmatic", "capricious"), 0, "\"resolute\": kararl�, azimli (ve tart��maya a��k, tutarl� ikileme)."),
                Question("sat_w_5", "The archival records shed light on an era that had previously been ______.", listOf("obscure", "celebrated", "conspicuous", "laudable"), 0, "\"obscure\": karanl�kta kalm��, az bilinen (shed light on obscure era).")
            )
        ),
        SeedExam(
            id = "exam-gre-sentence-eq",
            title = "GRE: Sentence Equivalence & Complex Context",
            durationMinutes = 15,
            examType = "GRE",
            levelBadge = "C2",
            difficulty = "Expert",
            questions = listOf(
                Question("gre_eq_1", "The critic dismissed the debut novel as ______, noting that its plot relied heavily on well-worn tropes.", listOf("derivative", "innovative", "trenchant", "heterogeneous"), 0, "\"derivative\": �zg�n olmayan, taklit (GRE Sentence Equivalence)."),
                Question("gre_eq_2", "Although the committee initially appeared ______, they eventually reached a unanimous consensus.", listOf("fractious", "monolithic", "congruous", "pliant"), 0, "\"fractious\": huysuz, uyu�maz, b�l�nm�� (\"Although... unanimous\")."),
                Question("gre_eq_3", "The executive's speech was characterized by ______; he spoke for an hour without clarifying his policy.", listOf("prolixity", "brevity", "succinctness", "candor"), 0, "\"prolixity\": gereksiz uzunluk, laf kalabal��� (GRE y�ksek frekans)."),
                Question("gre_eq_4", "The scientist remained ______ in the face of harsh criticism, refusing to retract her thesis.", listOf("intransigent", "vacillating", "malleable", "diffident"), 0, "\"intransigent\": tavizsiz, geri ad�m atmayan."),
                Question("gre_eq_5", "Select the synonym of \"SPECIOUS\":", listOf("plausible yet false", "empirically verified", "obscurely worded", "profoundly wise"), 0, "\"specious\": ilk bak��ta do�ru g�r�nen ama asl�nda yan�lt�c�/sahte.")
            )
        ),
        SeedExam(
            id = "exam-pte-fill-blanks",
            title = "PTE: Fill in the Blanks & Lexical Selection",
            durationMinutes = 12,
            examType = "PTE",
            levelBadge = "B2-C1",
            difficulty = "Medium",
            questions = listOf(
                Question("pte_fb_1", "Biomedical engineers are developing synthetic materials that can ______ natural bone tissue.", listOf("simulate", "disrupt", "counteract", "terminate"), 0, "\"simulate\": taklit etmek, benzer i�levi g�rmek (PTE Reading - Fill in Blanks)."),
                Question("pte_fb_2", "The committee reached a ______ decision after considering all stakeholder viewpoints.", listOf("unanimous", "discordant", "fragmented", "tentative"), 0, "\"unanimous decision\": oy birli�iyle al�nan karar."),
                Question("pte_fb_3", "Academic papers must cite previous literature to ______ academic integrity.", listOf("uphold", "suppress", "evade", "undermine"), 0, "\"uphold integrity\": d�r�stl��� ve ilkeleri korumak/s�rd�rmek."),
                Question("pte_fb_4", "The city's rapid expansion placed ______ pressure on water and sanitation systems.", listOf("unprecedented", "negligible", "trivial", "recurrent"), 0, "\"unprecedented pressure\": e�i benzeri g�r�lmemi� bask�."),
                Question("pte_fb_5", "Researchers noted a sharp ______ in infant mortality rates following the sanitation reform.", listOf("decline", "escalation", "elevation", "surge"), 0, "\"sharp decline\": keskin d����/azalma.")
            )
        ),
        SeedExam(
            id = "exam-det-interactive",
            title = "Duolingo English Test: Interactive Reading & Real Word ID",
            durationMinutes = 10,
            examType = "DET",
            levelBadge = "B2-C1",
            difficulty = "Medium",
            questions = listOf(
                Question("det_in_1", "Identify the correctly spelled English word:", listOf("Phenomenon", "Phenominon", "Phenomemon", "Phenomnon"), 0, "\"Phenomenon\": olgu, fenomen (DET do�ru kelimeyi tan�ma testi)."),
                Question("det_in_2", "The research findings will be ______ in an international peer-reviewed journal.", listOf("published", "propagated", "dispensed", "disclosed"), 0, "\"published in a journal\": dergide yay�mlanmak."),
                Question("det_in_3", "Select the word that fits: \"He is known for his ______ attention to detail.\"", listOf("meticulous", "careless", "superficial", "reckless"), 0, "\"meticulous attention\": titiz, k�l� k�rk yaran dikkat."),
                Question("det_in_4", "Choose the best connector: \"The team worked tirelessly; ______, they finished ahead of schedule.\"", listOf("consequently", "nevertheless", "otherwise", "conversely"), 0, "\"consequently\": dolay�s�yla, sonu� olarak."),
                Question("det_in_5", "Which word is an antonym of \"AMBIGUOUS\"?", listOf("Clear", "Vague", "Obscure", "Equivocal"), 0, "\"ambiguous\" = belirsiz/mu�lak; z�tt� \"clear\" = a��k/net.")
            )
        ),
        SeedExam(
            id = "exam-eiken-grade1",
            title = "EIKEN Grade 1: Global Affairs & Discourse Analysis",
            durationMinutes = 15,
            examType = "EIKEN",
            levelBadge = "C1",
            difficulty = "Hard",
            questions = listOf(
                Question("eiken_g1_1", "Demographic aging in industrialized nations poses severe ______ for pension solvency.", listOf("repercussions", "subsidies", "remedies", "dividends"), 0, "\"repercussions\": olumsuz yank�lar, ciddi sonu�lar (EIKEN Grade 1 sosyo-ekonomik soru)."),
                Question("eiken_g1_2", "International trade pacts aim to ______ non-tariff barriers between member states.", listOf("dismantle", "reinforce", "erect", "perpetuate"), 0, "\"dismantle barriers\": engelleri ortadan kald�rmak/y�kmak."),
                Question("eiken_g1_3", "The spokesperson gave a ______ response that failed to address the journalist's query directly.", listOf("circumspect", "candid", "blunt", "forthright"), 0, "\"circumspect\": temkinli, laf� �eviren, dikkatli."),
                Question("eiken_g1_4", "Clean water access is a fundamental human right that must not be ______.", listOf("compromised", "celebrated", "fostered", "sanctioned"), 0, "\"compromise a right\": bir haktan taviz vermek ya da tehlikeye atmak."),
                Question("eiken_g1_5", "Scholars emphasize the need to ______ traditional craftsmanship in rural regions.", listOf("preserve", "demolish", "deplete", "extinguish"), 0, "\"preserve craftsmanship\": zanaatkarl��� korumak ve ya�atmak.")
            )
        ),
        SeedExam(
            id = "exam-yds-connectors",
            title = "YDS / Y�KD�L: Ba�la�lar ve C�mle Tamamlama",
            durationMinutes = 15,
            examType = "YDS",
            levelBadge = "B2-C1",
            difficulty = "Medium",
            questions = listOf(
                Question("yds_c_1", "______ the severe blizzard, all international flights from the airport departed on schedule.", listOf("In spite of", "Because of", "Due to", "Owing to"), 0, "\"In spite of\": z�tl�k bildiren edat; ard�ndan isim gelir (\"the severe blizzard\")."),
                Question("yds_c_2", "The minister stated that the reform would proceed ______ public opposition intensified.", listOf("even if", "in case", "as long as", "so that"), 0, "\"even if\": -se bile (ko�ul ve z�tl�k ba�lac�)."),
                Question("yds_c_3", "Scientists must publish their data ______ other researchers can verify their findings.", listOf("so that", "lest", "unless", "in case of"), 0, "\"so that\": -ebilsin diye / amac�yla (ama� ba�lac�, ard�ndan can/could/would gelir)."),
                Question("yds_c_4", "______ had the new law been enacted than several legal challenges were filed.", listOf("No sooner", "Hardly", "Scarcely", "Barely"), 0, "\"No sooner... than\" kal�b�: devrik yap� gerektirir."),
                Question("yds_c_5", "The treaty was signed by all delegates, ______ bringing an end to years of territorial dispute.", listOf("thereby", "whereas", "otherwise", "nevertheless"), 0, "\"thereby + V-ing\": b�ylece, bu suretle (YDS y�ksek frekansl� sonu� zarf�)."),
                Question("yds_c_6", "______ had the economy shown signs of recovery when interest rates spiked again.", listOf("Hardly", "No sooner", "Rather", "Seldom"), 0, "\"Hardly... when\" zaman ve z�tl�k kal�b�."),
                Question("yds_c_7", "The archaeological expedition was postponed ______ adverse winter weather conditions.", listOf("on account of", "in contrast to", "with a view to", "as regards"), 0, "\"on account of\": -den dolay�, y�z�nden (neden-sonu� edat�)."),
                Question("yds_c_8", "We cannot approve the expenditure ______ a detailed cost breakdown is submitted.", listOf("unless", "provided that", "so long as", "if"), 0, "\"unless\": -medik�e, -mezse (olumsuz ko�ul)."),
                Question("yds_c_9", "______ she lacked formal credentials, her extensive field experience made her the ideal candidate.", listOf("Although", "Since", "Because", "Despite"), 0, "\"Although\": tam c�mle alan z�tl�k ba�lac� (c�mle: she lacked...)."),
                Question("yds_c_10", "The research grant was awarded ______ her groundbreaking paper on genetic engineering.", listOf("in recognition of", "in comparison with", "in place of", "by means of"), 0, "\"in recognition of\": -in takdiri/�erefine/tan�nmas� vesilesiyle.")
            )
        ),
        SeedExam(
            id = "exam-mixed-review",
            title = "Genel Tekrar",
            durationMinutes = 14,
            questions = listOf(
                Question("q1", "Neither of the students ______ the correct answer.", listOf("know", "knows", "knew", "have known"), 2, "\"Neither of + çoğul isim\" tekil kabul edilir; cümle bağlamı geçmiş zamanı gerektirir (knew)."),
                Question("q2", "Not only ______ late, but he also forgot his documents.", listOf("he was", "was he", "he is", "is he"), 1, "Olumsuzluk/sınırlama bildiren \"Not only\" cümle başına geldiğinde devrik yapı (inversion) gerekir: Not only + yardımcı fiil + özne."),
                Question("q3", "She is used to ______ up early.", listOf("get", "got", "getting", "gets"), 2, "\"be used to\" alışkanlık bildirir ve ardından fiil-ing gelir (\"to\" burada mastar değil, edattır)."),
                Question("q4", "I look forward to ______ from you soon.", listOf("hear", "hearing", "heard", "to hear"), 1, "\"look forward to\" ifadesinde \"to\" edattır, ardından fiil-ing gelir."),
                Question("q5", "Despite ______ hard, he failed the exam.", listOf("study", "studied", "to study", "studying"), 3, "\"despite\" bir edattır ve ardından fiil-ing (gerund) gelir."),
                Question("q6", "Neither the manager nor the employees ______ satisfied with the results.", listOf("was", "were", "is", "has been"), 1, "\"Neither...nor\" yapısında fiil, özneye en yakın olan isme (employees, çoğul) göre çekimlenir."),
                Question("q7", "He denied ______ the money.", listOf("to steal", "stealing", "steal", "stolen"), 1, "\"deny\" fiilinden sonra gerund (fiil-ing) gelir, \"to\" mastarı değil."),
                Question("q8", "It's high time you ______ your responsibilities seriously.", listOf("take", "took", "will take", "have taken"), 1, "\"It's (high) time + özne\" yapısından sonra Past Simple gelir; anlam şimdiki/gelecek zamana aittir ama biçim geçmiştir."),
                Question("q9", "Hardly ______ the show started when the lights went out.", listOf("has", "had", "did", "was"), 1, "\"Hardly...when\" yapısında devrik yapı ve Past Perfect kullanılır: Hardly had + özne + past participle."),
                Question("q10", "The more you practice, ______ you become.", listOf("better", "the better", "best", "the best"), 1, "\"The + comparative..., the + comparative...\" paralel karşılaştırma yapısı: bir şey arttıkça diğeri de artar/değişir.")
            )
        )
    )

    companion object {
        private const val TITLE_COLLECTION = "exam_titles"
        private const val TAG = "FirebaseExamRepo"
        private const val REVIEW_INTERVAL_FIRST_MS = 3L * 24 * 60 * 60 * 1000
        private const val REVIEW_INTERVAL_SECOND_MS = 7L * 24 * 60 * 60 * 1000
        private const val MASTERY_STREAK = 2
        private const val MICRO_EXAM_QUESTION_COUNT = 8
    }
}
