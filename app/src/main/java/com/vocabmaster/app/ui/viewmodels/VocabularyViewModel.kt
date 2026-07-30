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

    fun loadWords() {
        _uiState.value = ListUiState(isLoading = true)
        viewModelScope.launch {
            _uiState.value = when (val result = repository.getWordsToLearn()) {
                is Resource.Success -> ListUiState(isLoading = false, items = result.data)
                is Resource.Error -> ListUiState(isLoading = false, errorMessage = result.message)
            }
        }
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
