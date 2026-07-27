package com.example.globalyds.domain.repository

import com.example.globalyds.domain.models.Exam

interface ExamRepository {
    suspend fun getAvailableExams(): List<Exam>
    suspend fun getCompletedExams(): List<Exam>
    suspend fun submitExamResult(examId: String, score: Int)
}
