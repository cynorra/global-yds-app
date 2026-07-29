package com.vocabmaster.app.data.repository

import com.vocabmaster.app.domain.models.Exam
import com.vocabmaster.app.domain.models.Question
import com.vocabmaster.app.domain.repository.ExamRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseExamRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ExamRepository {

    private val examsCollection = firestore.collection("exams")

    override suspend fun getAvailableExams(): List<Exam> {
        return try {
            seedSampleExamIfEmpty()
            examsCollection.whereEqualTo("isCompleted", false).get().await()
                .documents.mapNotNull { it.toObject(Exam::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getCompletedExams(): List<Exam> {
        return try {
            examsCollection.whereEqualTo("isCompleted", true).get().await()
                .documents.mapNotNull { it.toObject(Exam::class.java) }
        } catch (e: Exception) {
            emptyList()
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

    override suspend fun submitExamResult(examId: String, score: Int) {
        try {
            examsCollection.document(examId)
                .update(mapOf("isCompleted" to true, "score" to score))
                .await()
        } catch (e: Exception) {
            // Swallow network/Firestore errors; caller can retry the submission later.
        }
    }

    // Seeds a single sample exam + a few sample questions the first time the collection is
    // empty, so the exam-taking flow is demoable before a real question bank is populated.
    private suspend fun seedSampleExamIfEmpty() {
        val isEmpty = examsCollection.limit(1).get().await().isEmpty
        if (!isEmpty) return

        val examId = "sample-1"
        examsCollection.document(examId).set(
            mapOf(
                "id" to examId,
                "title" to "Örnek YDS Sınavı",
                "totalQuestions" to sampleQuestions.size,
                "durationMinutes" to 10,
                "isCompleted" to false,
                "score" to 0
            )
        ).await()

        val questionsCollection = examsCollection.document(examId).collection("questions")
        sampleQuestions.forEach { question ->
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

    private val sampleQuestions = listOf(
        Question(
            id = "q1",
            text = "She ______ to the store every day.",
            options = listOf("go", "goes", "going", "gone"),
            correctOptionIndex = 1
        ),
        Question(
            id = "q2",
            text = "The meeting was ______ due to bad weather.",
            options = listOf("cancelled", "cancel", "cancelling", "cancels"),
            correctOptionIndex = 0
        ),
        Question(
            id = "q3",
            text = "By the time we arrived, the movie ______.",
            options = listOf("already started", "had already started", "has already started", "already starts"),
            correctOptionIndex = 1
        )
    )
}
