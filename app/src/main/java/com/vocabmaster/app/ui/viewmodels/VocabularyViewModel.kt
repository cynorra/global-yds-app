package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.Word
import com.vocabmaster.app.domain.repository.VocabularyRepository
import kotlinx.coroutines.launch

class VocabularyViewModel(private val repository: VocabularyRepository) : ViewModel() {

    private val _uiState = MutableLiveData(ListUiState<Word>())
    val uiState: LiveData<ListUiState<Word>> get() = _uiState

    private var allCachedWords: List<Word> = emptyList()
    private var currentExamFilter: String = "ALL"

    fun loadWords() {
        _uiState.value = ListUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.getWordsToLearn()) {
                is Resource.Success -> {
                    allCachedWords = result.data
                    applyCurrentFilter()
                }
                is Resource.Error -> {
                    _uiState.value = ListUiState(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun filterWordsByExam(examType: String) {
        currentExamFilter = examType
        applyCurrentFilter()
    }

    private fun applyCurrentFilter() {
        val filtered = if (currentExamFilter.equals("ALL", ignoreCase = true)) {
            allCachedWords
        } else {
            allCachedWords.filter { it.targetExam.equals(currentExamFilter, ignoreCase = true) || it.targetExam == "ALL" }
        }
        _uiState.value = ListUiState(isLoading = false, items = filtered)
    }

    fun markWordAsLearned(wordId: String) {
        viewModelScope.launch {
            repository.markWordAsLearned(wordId)
            loadWords()
        }
    }

    fun deleteWord(wordId: String) {
        viewModelScope.launch {
            repository.deleteWord(wordId)
            loadWords()
        }
    }
}
