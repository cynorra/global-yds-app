package com.vocabmaster.app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vocabmaster.app.MainDispatcherRule
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.GrammarTopic
import com.vocabmaster.app.fakes.FakeGrammarRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GrammarViewModelTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadTopics on success populates items and clears loading`() {
        val repository = FakeGrammarRepository().apply {
            topics = Resource.Success(
                listOf(GrammarTopic(id = "1", title = "Passive Voice", explanation = "..."))
            )
        }
        val viewModel = GrammarViewModel(repository)

        viewModel.loadTopics()

        val state = viewModel.uiState.value!!
        assertFalse(state.isLoading)
        assertEquals(1, state.items.size)
        assertEquals("Passive Voice", state.items.first().title)
        assertEquals(null, state.errorMessage)
    }

    @Test
    fun `loadTopics on failure surfaces the error message instead of an empty list`() {
        val repository = FakeGrammarRepository().apply {
            topics = Resource.Error("network down")
        }
        val viewModel = GrammarViewModel(repository)

        viewModel.loadTopics()

        val state = viewModel.uiState.value!!
        assertFalse(state.isLoading)
        assertTrue(state.items.isEmpty())
        assertEquals("network down", state.errorMessage)
    }

    @Test
    fun `initial state before loading is a loading state`() {
        val viewModel = GrammarViewModel(FakeGrammarRepository())

        assertTrue(viewModel.uiState.value!!.isLoading)
    }
}
