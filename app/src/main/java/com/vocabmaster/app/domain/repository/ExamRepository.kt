package com.vocabmaster.app.domain.repository

import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.Exam
import com.vocabmaster.app.domain.models.ExamAttempt
import com.vocabmaster.app.domain.models.Question

interface ExamRepository {
    suspend fun getAllExams(): Resource<List<Exam>>
    suspend fun getCompletedExams(): Resource<List<ExamAttempt>>
    suspend fun getExamQuestions(examId: String): List<Question>
    suspend fun submitExamResult(examId: String, examTitle: String, score: Int, mistakes: List<Question>)
    suspend fun getMistakes(): List<Question>
    suspend fun recordMistakeReview(questionId: String, wasCorrect: Boolean)
}
