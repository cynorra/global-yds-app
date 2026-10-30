package com.vocabmaster.app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vocabmaster.app.MainDispatcherRule
import com.vocabmaster.app.domain.models.Word
import com.vocabmaster.app.fakes.FakeVocabularyRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class VocabularyViewModelTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val word1 = Word(id = "1", englishWord = "Abandon", turkishMeaning = "terk etmek")
    private val word2 = Word(id = "2", englishWord = "Achieve", turkishMeaning = "başarmak")

    @Test
    fun `loadWords shows only unlearned words`() {
        val repository = FakeVocabularyRepository(listOf(word1, word2.copy(isLearned = true)))
        val viewModel = VocabularyViewModel(repository)

        viewModel.loadWords()

        val state = viewModel.uiState.value!!
        assertFalse(state.isLoading)
        assertEquals(listOf("Abandon"), state.items.map { it.englishWord })
    }

    @Test
    fun `markWordAsLearned removes the word from the to-learn list on reload`() {
        val repository = FakeVocabularyRepository(listOf(word1, word2))
        val viewModel = VocabularyViewModel(repository)
        viewModel.loadWords()
        assertEquals(2, viewModel.uiState.value!!.items.size)

        viewModel.markWordAsLearned(word1.id)

        val state = viewModel.uiState.value!!
        assertEquals(listOf("Achieve"), state.items.map { it.englishWord })
    }

    @Test
    fun `deleteWord removes the word entirely on reload`() {
        val repository = FakeVocabularyRepository(listOf(word1, word2))
        val viewModel = VocabularyViewModel(repository)
        viewModel.loadWords()

        viewModel.deleteWord(word1.id)

        val state = viewModel.uiState.value!!
        assertEquals(listOf("Achieve"), state.items.map { it.englishWord })
    }

    @Test
    fun `loadWords on repository error surfaces an error state, not an empty list`() {
        val repository = FakeVocabularyRepository(listOf(word1)).apply { shouldError = true }
        val viewModel = VocabularyViewModel(repository)

        viewModel.loadWords()

        val state = viewModel.uiState.value!!
        assertTrue(state.items.isEmpty())
        assertEquals("boom", state.errorMessage)
    }

    @Test
    fun `filterWordsByExam filters words by targetExam and ALL returns all`() {
        val wToeic = Word(id = "1", englishWord = "Compliance", targetExam = "TOEIC")
        val wIelts = Word(id = "2", englishWord = "Corroborate", targetExam = "IELTS")
        val wGeneral = Word(id = "3", englishWord = "Common", targetExam = "ALL")

        val repository = FakeVocabularyRepository(listOf(wToeic, wIelts, wGeneral))
        val viewModel = VocabularyViewModel(repository)
        viewModel.loadWords()

        assertEquals(3, viewModel.uiState.value!!.items.size)

        viewModel.filterWordsByExam("TOEIC")
        val toeicWords = viewModel.uiState.value!!.items.map { it.englishWord }
        assertTrue(toeicWords.contains("Compliance"))
        assertTrue(toeicWords.contains("Common"))
        assertFalse(toeicWords.contains("Corroborate"))

        viewModel.filterWordsByExam("IELTS")
        val ieltsWords = viewModel.uiState.value!!.items.map { it.englishWord }
        assertTrue(ieltsWords.contains("Corroborate"))
        assertTrue(ieltsWords.contains("Common"))
        assertFalse(ieltsWords.contains("Compliance"))

        viewModel.filterWordsByExam("ALL")
        assertEquals(3, viewModel.uiState.value!!.items.size)
    }
}
