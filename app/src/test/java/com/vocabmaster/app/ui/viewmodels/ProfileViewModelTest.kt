package com.vocabmaster.app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vocabmaster.app.MainDispatcherRule
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.ExamAttempt
import com.vocabmaster.app.domain.models.Word
import com.vocabmaster.app.fakes.FakeExamRepository
import com.vocabmaster.app.fakes.FakeVocabularyRepository
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadStats reports the learned word count and every completed exam score`() {
        val vocabularyRepository = FakeVocabularyRepository(
            listOf(
                Word(id = "1", englishWord = "Abandon", turkishMeaning = "terk etmek", isLearned = true),
                Word(id = "2", englishWord = "Achieve", turkishMeaning = "başarmak", isLearned = true),
                Word(id = "3", englishWord = "Adjust", turkishMeaning = "uyum sağlamak")
            )
        )
        val examRepository = FakeExamRepository().apply {
            completedExams = Resource.Success(
                listOf(ExamAttempt(examId = "e1", score = 80), ExamAttempt(examId = "e2", score = 60))
            )
        }
        val viewModel = ProfileViewModel(vocabularyRepository, examRepository)

        viewModel.loadStats()

        val stats = viewModel.stats.value!!
        assertEquals(2, stats.learnedWordsCount)
        assertEquals(listOf(80, 60), stats.completedExamScores)
    }

    @Test
    fun `loadStats degrades to zero-and-empty instead of crashing when both sources fail`() {
        val vocabularyRepository = FakeVocabularyRepository(emptyList()).apply { shouldError = true }
        val examRepository = FakeExamRepository().apply { completedExams = Resource.Error("offline") }
        val viewModel = ProfileViewModel(vocabularyRepository, examRepository)

        viewModel.loadStats()

        val stats = viewModel.stats.value!!
        assertEquals(0, stats.learnedWordsCount)
        assertEquals(emptyList<Int>(), stats.completedExamScores)
    }
}
