package com.vocabmaster.app.data.repository

import com.vocabmaster.app.data.remote.TranslationApi
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.Exam
import com.vocabmaster.app.domain.models.ExamAttempt
import com.vocabmaster.app.domain.models.Question
import com.vocabmaster.app.domain.repository.ExamRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
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

    // Exam titles are authored in Turkish in the shared `exams` bank (see seedSampleExamsIfEmpty).
    // getAllExams() returns them translated into the device's language, cached per language in
    // Firestore so the same title is only ever translated once across all devices.
    override suspend fun getAllExams(): Resource<List<Exam>> {
        return try {
            seedSampleExamsIfEmpty()
            val exams = examsCollection.get().await().documents.mapNotNull { it.toObject(Exam::class.java) }
            Resource.Success(withTranslatedTitles(exams))
        } catch (e: Exception) {
            Resource.Error(e.message)
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
        return try {
            examsCollection.document(examId).collection("questions").get().await()
                .documents.mapNotNull { it.toObject(Question::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun submitExamResult(examId: String, examTitle: String, score: Int) {
        val uid = currentUserId ?: return
        try {
            examAttemptsCollection(uid).document(examId).set(
                mapOf(
                    "examId" to examId,
                    "examTitle" to examTitle,
                    "score" to score,
                    "completedAt" to System.currentTimeMillis()
                )
            ).await()
        } catch (e: Exception) {
            // Swallow network/Firestore errors; caller can retry the submission later.
        }
    }

    // Seeds the shared exam question bank the first time the collection is empty, so the
    // exam-taking flow is demoable before a real question bank is populated. This is per-bank,
    // not per-user: unlike attempts, the bank itself is shared by everyone.
    private suspend fun seedSampleExamsIfEmpty() {
        val isEmpty = examsCollection.limit(1).get().await().isEmpty
        if (!isEmpty) return

        seedExams.forEach { seedExam ->
            examsCollection.document(seedExam.id).set(
                mapOf(
                    "id" to seedExam.id,
                    "title" to seedExam.title,
                    "totalQuestions" to seedExam.questions.size,
                    "durationMinutes" to seedExam.durationMinutes
                )
            ).await()

            val questionsCollection = examsCollection.document(seedExam.id).collection("questions")
            seedExam.questions.forEach { question ->
                questionsCollection.document(question.id).set(
                    mapOf(
                        "id" to question.id,
                        "text" to question.text,
                        "options" to question.options,
                        "correctOptionIndex" to question.correctOptionIndex
                    )
                ).await()
            }
        }
    }

    private data class SeedExam(
        val id: String,
        val title: String,
        val durationMinutes: Int,
        val questions: List<Question>
    )

    private val seedExams = listOf(
        SeedExam(
            id = "exam-tenses",
            title = "Zamanlar (Tenses)",
            durationMinutes = 12,
            questions = listOf(
                Question("q1", "She ______ to the store every day.", listOf("go", "goes", "going", "gone"), 1),
                Question("q2", "By the time we arrived, the movie ______.", listOf("already started", "had already started", "has already started", "already starts"), 1),
                Question("q3", "I ______ my homework since 6 p.m.", listOf("do", "did", "have been doing", "will do"), 2),
                Question("q4", "They ______ dinner when the phone rang.", listOf("have", "had", "were having", "has"), 2),
                Question("q5", "She ______ abroad twice this year.", listOf("traveled", "has traveled", "was traveling", "travels"), 1),
                Question("q6", "Next week, I ______ my grandmother in London.", listOf("visit", "visited", "am visiting", "will have visited"), 2),
                Question("q7", "He ______ for the company since 2015.", listOf("works", "worked", "has worked", "is working"), 2),
                Question("q8", "While she ______ dinner, the lights went out.", listOf("cooked", "was cooking", "cooks", "has cooked"), 1),
                Question("q9", "The train ______ before we got to the station.", listOf("left", "has left", "had left", "leaves"), 2),
                Question("q10", "Look! The bus ______.", listOf("comes", "came", "is coming", "has come"), 2)
            )
        ),
        SeedExam(
            id = "exam-conditionals-modals",
            title = "Koşul Cümleleri ve Modallar",
            durationMinutes = 10,
            questions = listOf(
                Question("q1", "If it rains tomorrow, we ______ the picnic.", listOf("cancel", "will cancel", "would cancel", "canceled"), 1),
                Question("q2", "If I were you, I ______ that job offer.", listOf("accept", "will accept", "would accept", "accepted"), 2),
                Question("q3", "If she had studied harder, she ______ the exam.", listOf("would pass", "will pass", "would have passed", "passes"), 2),
                Question("q4", "You ______ smoke in this building; it's strictly forbidden.", listOf("don't have to", "must not", "may not", "shouldn't"), 1),
                Question("q5", "She ______ be at home now; I saw her car in the driveway.", listOf("must", "can't", "mustn't", "should"), 0),
                Question("q6", "He ______ have left already; his coat is still here.", listOf("must", "can't", "might", "should"), 1),
                Question("q7", "Passengers ______ fasten their seatbelts during takeoff.", listOf("might", "could", "must", "would"), 2),
                Question("q8", "If only I ______ more time to prepare for the interview.", listOf("have", "had", "had had", "would have"), 2)
            )
        ),
        SeedExam(
            id = "exam-passive-reported",
            title = "Edilgen Çatı ve Bildirilen Konuşma",
            durationMinutes = 10,
            questions = listOf(
                Question("q1", "The report ______ by the manager yesterday.", listOf("wrote", "was written", "is written", "has written"), 1),
                Question("q2", "This bridge ______ in 1990.", listOf("built", "was built", "has built", "is building"), 1),
                Question("q3", "English ______ in many countries around the world.", listOf("speaks", "is spoken", "spoke", "was speaking"), 1),
                Question("q4", "The letter ______ by tomorrow morning.", listOf("will send", "will be sent", "sends", "sent"), 1),
                Question("q5", "She said that she ______ tired.", listOf("is", "was", "has been", "will be"), 1),
                Question("q6", "He told me that he ______ the movie the day before.", listOf("watches", "watched", "had watched", "has watched"), 2),
                Question("q7", "They said they ______ back the following week.", listOf("come", "came", "will come", "would come"), 3),
                Question("q8", "The teacher asked the students ______ the exercise quietly.", listOf("do", "doing", "to do", "did"), 2)
            )
        ),
        SeedExam(
            id = "exam-relative-comparatives",
            title = "İlgi Cümlecikleri ve Karşılaştırmalar",
            durationMinutes = 10,
            questions = listOf(
                Question("q1", "The man ______ car was stolen called the police.", listOf("who", "whose", "which", "that"), 1),
                Question("q2", "This is the restaurant ______ we had dinner last night.", listOf("which", "who", "where", "whose"), 2),
                Question("q3", "The book, ______ was written by a famous author, became a bestseller.", listOf("that", "which", "who", "whose"), 1),
                Question("q4", "She is the woman ______ helped me find my way.", listOf("whom", "who", "which", "whose"), 1),
                Question("q5", "This exam is ______ than the last one.", listOf("difficult", "more difficult", "most difficult", "the most difficult"), 1),
                Question("q6", "He is ______ student in the whole class.", listOf("hardworking", "more hardworking", "the most hardworking", "hardworking than"), 2),
                Question("q7", "This is by far ______ hotel I have ever stayed in.", listOf("good", "better", "the best", "best"), 2),
                Question("q8", "The weather today is ______ than yesterday.", listOf("bad", "worse", "the worst", "more bad"), 1)
            )
        ),
        SeedExam(
            id = "exam-mixed-review",
            title = "Genel Tekrar",
            durationMinutes = 14,
            questions = listOf(
                Question("q1", "Neither of the students ______ the correct answer.", listOf("know", "knows", "knew", "have known"), 2),
                Question("q2", "Not only ______ late, but he also forgot his documents.", listOf("he was", "was he", "he is", "is he"), 1),
                Question("q3", "She is used to ______ up early.", listOf("get", "got", "getting", "gets"), 2),
                Question("q4", "I look forward to ______ from you soon.", listOf("hear", "hearing", "heard", "to hear"), 1),
                Question("q5", "Despite ______ hard, he failed the exam.", listOf("study", "studied", "to study", "studying"), 3),
                Question("q6", "Neither the manager nor the employees ______ satisfied with the results.", listOf("was", "were", "is", "has been"), 1),
                Question("q7", "He denied ______ the money.", listOf("to steal", "stealing", "steal", "stolen"), 1),
                Question("q8", "It's high time you ______ your responsibilities seriously.", listOf("take", "took", "will take", "have taken"), 1),
                Question("q9", "Hardly ______ the show started when the lights went out.", listOf("has", "had", "did", "was"), 1),
                Question("q10", "The more you practice, ______ you become.", listOf("better", "the better", "best", "the best"), 1)
            )
        )
    )

    companion object {
        private const val TITLE_COLLECTION = "exam_titles"
    }
}
