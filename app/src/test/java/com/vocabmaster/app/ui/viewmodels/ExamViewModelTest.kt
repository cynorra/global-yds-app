package com.vocabmaster.app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vocabmaster.app.MainDispatcherRule
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.Exam
import com.vocabmaster.app.domain.models.ExamAttempt
import com.vocabmaster.app.fakes.FakeExamRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ExamViewModelTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val exam = Exam(id = "exam-1", title = "Tenses", totalQuestions = 10, durationMinutes = 12)

    @Test
    fun `completed exams are marked completed with their score`() {
        val repository = FakeExamRepository().apply {
            exams = Resource.Success(listOf(exam))
            completedExams = Resource.Success(listOf(ExamAttempt(examId = "exam-1", score = 80)))
        }
        val viewModel = ExamViewModel(repository)

        viewModel.loadExams()

        val item = viewModel.uiState.value!!.items.single()
        assertTrue(item.isCompleted)
        assertEquals(80, item.score)
    }

    @Test
    fun `if fetching completed attempts fails, the exam list still loads uncompleted`() {
        val repository = FakeExamRepository().apply {
            exams = Resource.Success(listOf(exam))
            completedExams = Resource.Error("offline")
        }
        val viewModel = ExamViewModel(repository)

        viewModel.loadExams()

        val state = viewModel.uiState.value!!
        assertFalse(state.isLoading)
        assertEquals(null, state.errorMessage)
        val item = state.items.single()
        assertFalse(item.isCompleted)
        assertEquals(0, item.score)
    }

    @Test
    fun `if fetching the exam list itself fails, the screen shows an error`() {
        val repository = FakeExamRepository().apply {
            exams = Resource.Error("network down")
        }
        val viewModel = ExamViewModel(repository)

        viewModel.loadExams()

        val state = viewModel.uiState.value!!
        assertTrue(state.items.isEmpty())
        assertEquals("network down", state.errorMessage)
    }

    @Test
    fun `weak area insight points at an unattempted topic before a low-scoring one`() {
        val exams = listOf(
            Exam(id = "exam-tenses", title = "Tenses", totalQuestions = 10, durationMinutes = 12),
            Exam(id = "exam-conditionals-modals", title = "Conditionals", totalQuestions = 8, durationMinutes = 10)
        )
        val repository = FakeExamRepository().apply {
            this.exams = Resource.Success(exams)
            completedExams = Resource.Success(listOf(ExamAttempt(examId = "exam-tenses", score = 40)))
        }
        val viewModel = ExamViewModel(repository)

        viewModel.loadExams()

        val insight = viewModel.weakAreaInsight.value!!
        assertEquals("Conditionals", insight.topicTitle)
        assertEquals(null, insight.scorePercent)
    }

    @Test
    fun `weak area insight falls back to the lowest-scoring topic once everything is attempted`() {
        val exams = listOf(
            Exam(id = "exam-tenses", title = "Tenses", totalQuestions = 10, durationMinutes = 12),
            Exam(id = "exam-conditionals-modals", title = "Conditionals", totalQuestions = 8, durationMinutes = 10)
        )
        val repository = FakeExamRepository().apply {
            this.exams = Resource.Success(exams)
            completedExams = Resource.Success(
                listOf(
                    ExamAttempt(examId = "exam-tenses", score = 90),
                    ExamAttempt(examId = "exam-conditionals-modals", score = 45)
                )
            )
        }
        val viewModel = ExamViewModel(repository)

        viewModel.loadExams()

        val insight = viewModel.weakAreaInsight.value!!
        assertEquals("Conditionals", insight.topicTitle)
        assertEquals(45, insight.scorePercent)
    }

    @Test
    fun `weak area insight is null once every topic is mastered`() {
        val exams = listOf(Exam(id = "exam-tenses", title = "Tenses", totalQuestions = 10, durationMinutes = 12))
        val repository = FakeExamRepository().apply {
            this.exams = Resource.Success(exams)
            completedExams = Resource.Success(listOf(ExamAttempt(examId = "exam-tenses", score = 100)))
        }
        val viewModel = ExamViewModel(repository)

        viewModel.loadExams()

        assertEquals(null, viewModel.weakAreaInsight.value)
    }

    @Test
    fun `weak area insight ignores the daily exam since it's a mixed pool, not a single topic`() {
        val exams = listOf(
            Exam(id = "exam-tenses", title = "Tenses", totalQuestions = 10, durationMinutes = 12),
            Exam(id = "exam-daily-2026-09-15", title = "Exam of the Day", totalQuestions = 10, durationMinutes = 15)
        )
        val repository = FakeExamRepository().apply {
            this.exams = Resource.Success(exams)
            completedExams = Resource.Success(listOf(ExamAttempt(examId = "exam-tenses", score = 100)))
        }
        val viewModel = ExamViewModel(repository)

        viewModel.loadExams()

        assertEquals(null, viewModel.weakAreaInsight.value)
    }

    @Test
    fun `filterByExamType filters exams by matching examType and ALL returns all`() {
        val exams = listOf(
            Exam(id = "exam-toeic", title = "TOEIC Test", examType = "TOEIC"),
            Exam(id = "exam-ielts", title = "IELTS Test", examType = "IELTS"),
            Exam(id = "exam-yds", title = "YDS Test", examType = "YDS")
        )
        val repository = FakeExamRepository().apply {
            this.exams = Resource.Success(exams)
            completedExams = Resource.Success(emptyList())
        }
        val viewModel = ExamViewModel(repository)
        viewModel.loadExams()

        assertEquals(3, viewModel.uiState.value!!.items.size)

        viewModel.filterByExamType("TOEIC")
        assertEquals(1, viewModel.uiState.value!!.items.size)
        assertEquals("exam-toeic", viewModel.uiState.value!!.items[0].id)

        viewModel.filterByExamType("IELTS")
        assertEquals(1, viewModel.uiState.value!!.items.size)
        assertEquals("exam-ielts", viewModel.uiState.value!!.items[0].id)

        viewModel.filterByExamType("ALL")
        assertEquals(3, viewModel.uiState.value!!.items.size)
    }
}
