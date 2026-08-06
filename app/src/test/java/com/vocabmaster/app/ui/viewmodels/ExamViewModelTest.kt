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
}
