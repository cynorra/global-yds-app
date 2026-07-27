package com.vocabmaster.app.data.repository

import com.vocabmaster.app.domain.models.Exam
import com.vocabmaster.app.domain.repository.ExamRepository
import kotlinx.coroutines.delay

class MockExamRepositoryImpl : ExamRepository {
    private val exams = mutableListOf(
        Exam("1", "YDS İlkbahar 2023", 80, 180, false, 0),
        Exam("2", "YDS Sonbahar 2022", 80, 180, true, 65),
        Exam("3", "Mini Kelime Testi", 20, 30, false, 0)
    )

    override suspend fun getAvailableExams(): List<Exam> {
        delay(500)
        return exams.filter { !it.isCompleted }
    }

    override suspend fun getCompletedExams(): List<Exam> {
        delay(500)
        return exams.filter { it.isCompleted }
    }

    override suspend fun submitExamResult(examId: String, score: Int) {
        delay(500)
        val index = exams.indexOfFirst { it.id == examId }
        if (index != -1) {
            exams[index] = exams[index].copy(isCompleted = true, score = score)
        }
    }
}
