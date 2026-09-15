package com.vocabmaster.app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vocabmaster.app.MainDispatcherRule
import com.vocabmaster.app.domain.models.Question
import com.vocabmaster.app.fakes.FakeExamRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ExamTakingViewModelTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val questions = listOf(
        Question(id = "q1", text = "She ___ every day.", options = listOf("go", "goes"), correctOptionIndex = 1),
        Question(id = "q2", text = "They ___ home.", options = listOf("went", "goed"), correctOptionIndex = 0),
        Question(id = "q3", text = "I ___ tired.", options = listOf("am", "is"), correctOptionIndex = 0)
    )

    private fun viewModelWithLoadedQuestions(repository: FakeExamRepository): ExamTakingViewModel {
        repository.questionsByExamId = mapOf("exam-1" to questions)
        val viewModel = ExamTakingViewModel(repository)
        viewModel.loadQuestions("exam-1", "Tenses", durationMinutes = 15)
        return viewModel
    }

    @Test
    fun `loadQuestions exposes the first question and total count`() {
        val viewModel = viewModelWithLoadedQuestions(FakeExamRepository())

        assertEquals(3, viewModel.totalQuestions.value)
        assertEquals(0, viewModel.currentIndex.value)
        assertEquals(questions[0], viewModel.currentQuestion.value)
    }

    @Test
    fun `goToNextQuestion advances through all questions in order`() {
        val viewModel = viewModelWithLoadedQuestions(FakeExamRepository())

        viewModel.goToNextQuestion("exam-1")
        assertEquals(1, viewModel.currentIndex.value)
        assertEquals(questions[1], viewModel.currentQuestion.value)

        viewModel.goToNextQuestion("exam-1")
        assertEquals(2, viewModel.currentIndex.value)
        assertEquals(questions[2], viewModel.currentQuestion.value)
    }

    @Test
    fun `isLastQuestion is only true on the final question`() {
        val viewModel = viewModelWithLoadedQuestions(FakeExamRepository())

        assertEquals(false, viewModel.isLastQuestion())
        viewModel.goToNextQuestion("exam-1")
        assertEquals(false, viewModel.isLastQuestion())
        viewModel.goToNextQuestion("exam-1")
        assertTrue(viewModel.isLastQuestion())
    }

    @Test
    fun `answering every question correctly scores 100 percent and submits the result`() {
        val repository = FakeExamRepository()
        val viewModel = viewModelWithLoadedQuestions(repository)

        viewModel.selectAnswer(1) // correct for q1
        viewModel.goToNextQuestion("exam-1")
        viewModel.selectAnswer(0) // correct for q2
        viewModel.goToNextQuestion("exam-1")
        viewModel.selectAnswer(0) // correct for q3
        viewModel.goToNextQuestion("exam-1") // past the last question -> finishes the exam

        assertEquals(100, viewModel.finalScorePercent.value)
        assertEquals(Triple("exam-1", "Tenses", 100), repository.submittedResults.single())
    }

    @Test
    fun `partial correct answers score proportionally`() {
        val repository = FakeExamRepository()
        val viewModel = viewModelWithLoadedQuestions(repository)

        viewModel.selectAnswer(1) // correct
        viewModel.goToNextQuestion("exam-1")
        viewModel.selectAnswer(1) // wrong (correct is 0)
        viewModel.goToNextQuestion("exam-1")
        viewModel.selectAnswer(1) // wrong (correct is 0)
        viewModel.goToNextQuestion("exam-1")

        // 1 out of 3 correct = 33%
        assertEquals(33, viewModel.finalScorePercent.value)
    }

    @Test
    fun `an unanswered question counts as incorrect rather than crashing`() {
        val repository = FakeExamRepository()
        val viewModel = viewModelWithLoadedQuestions(repository)

        // Skip answering q1 entirely, answer q2 and q3 correctly.
        viewModel.goToNextQuestion("exam-1")
        viewModel.selectAnswer(0)
        viewModel.goToNextQuestion("exam-1")
        viewModel.selectAnswer(0)
        viewModel.goToNextQuestion("exam-1")

        assertEquals(66, viewModel.finalScorePercent.value)
    }
}
