package com.vocabmaster.app.domain.repository

import com.vocabmaster.app.domain.models.Exam

interface ExamRepository {
    suspend fun getAvailableExams(): List<Exam>
    suspend fun getCompletedExams(): List<Exam>
    suspend fun submitExamResult(examId: String, score: Int)
}
