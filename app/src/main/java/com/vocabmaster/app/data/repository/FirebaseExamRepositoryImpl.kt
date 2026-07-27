package com.vocabmaster.app.data.repository

import com.vocabmaster.app.domain.models.Exam
import com.vocabmaster.app.domain.repository.ExamRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseExamRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ExamRepository {

    private val examsCollection = firestore.collection("exams")

    override suspend fun getAvailableExams(): List<Exam> {
        return try {
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

    override suspend fun submitExamResult(examId: String, score: Int) {
        try {
            examsCollection.document(examId)
                .update(mapOf("isCompleted" to true, "score" to score))
                .await()
        } catch (e: Exception) {
            // Swallow network/Firestore errors; caller can retry the submission later.
        }
    }
}
