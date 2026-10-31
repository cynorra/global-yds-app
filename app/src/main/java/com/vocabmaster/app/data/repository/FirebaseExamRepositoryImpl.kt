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
        val snapshot = try { examsCollection.limit(1).get().await() } catch (e: Exception) { return }
        if (!snapshot.isEmpty) return

        seedExams.take(5).forEach { seedExam ->
            val docRef = examsCollection.document(seedExam.id)
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

    private data class SeedExam(
        val id: String,
        val title: String,
        val durationMinutes: Int,
        val examType: String = "ALL",
        val levelBadge: String = "B2",
        val difficulty: String = "Medium",
        val questions: List<Question>
    )

    private val seedExams: List<SeedExam>
        get() {
            return com.vocabmaster.app.data.local.AssetDataLoader.loadExams().map { jsonExam ->
                SeedExam(
                    id = jsonExam.id,
                    title = jsonExam.title,
                    durationMinutes = jsonExam.durationMinutes,
                    examType = jsonExam.examType,
                    levelBadge = jsonExam.levelBadge,
                    difficulty = jsonExam.difficulty,
                    questions = jsonExam.questions
                )
            }
        }

    companion object {
        private const val TITLE_COLLECTION = "exam_titles"
        private const val TAG = "FirebaseExamRepo"
        private const val REVIEW_INTERVAL_FIRST_MS = 3L * 24 * 60 * 60 * 1000
        private const val REVIEW_INTERVAL_SECOND_MS = 7L * 24 * 60 * 60 * 1000
        private const val MASTERY_STREAK = 2
        private const val MICRO_EXAM_QUESTION_COUNT = 8
    }
}
