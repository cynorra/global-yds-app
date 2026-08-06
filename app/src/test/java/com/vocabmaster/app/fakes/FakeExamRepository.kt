package com.vocabmaster.app.fakes

import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.Exam
import com.vocabmaster.app.domain.models.ExamAttempt
import com.vocabmaster.app.domain.models.Question
import com.vocabmaster.app.domain.repository.ExamRepository

class FakeExamRepository : ExamRepository {

    var exams: Resource<List<Exam>> = Resource.Success(emptyList())
    var completedExams: Resource<List<ExamAttempt>> = Resource.Success(emptyList())
    var questionsByExamId: Map<String, List<Question>> = emptyMap()

    val submittedResults = mutableListOf<Triple<String, String, Int>>()

    override suspend fun getAllExams(): Resource<List<Exam>> = exams

    override suspend fun getCompletedExams(): Resource<List<ExamAttempt>> = completedExams

    override suspend fun getExamQuestions(examId: String): List<Question> =
        questionsByExamId[examId].orEmpty()

    override suspend fun submitExamResult(examId: String, examTitle: String, score: Int) {
        submittedResults.add(Triple(examId, examTitle, score))
    }
}
