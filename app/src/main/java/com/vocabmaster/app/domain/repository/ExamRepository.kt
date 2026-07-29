package com.vocabmaster.app.domain.repository

import com.vocabmaster.app.domain.models.Exam
import com.vocabmaster.app.domain.models.Question

interface ExamRepository {
    suspend fun getAvailableExams(): List<Exam>
    suspend fun getCompletedExams(): List<Exam>
    suspend fun getExamQuestions(examId: String): List<Question>
    suspend fun submitExamResult(examId: String, score: Int)
}
