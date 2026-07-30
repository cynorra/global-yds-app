package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.GrammarTopic
import com.vocabmaster.app.domain.repository.GrammarRepository
import kotlinx.coroutines.launch

class GrammarViewModel(private val repository: GrammarRepository) : ViewModel() {

    private val _uiState = MutableLiveData(ListUiState<GrammarTopic>())
    val uiState: LiveData<ListUiState<GrammarTopic>> get() = _uiState

    fun loadTopics() {
        _uiState.value = ListUiState(isLoading = true)
        viewModelScope.launch {
            _uiState.value = when (val result = repository.getTopics()) {
                is Resource.Success -> ListUiState(isLoading = false, items = result.data)
                is Resource.Error -> ListUiState(isLoading = false, errorMessage = result.message)
            }
        }
    }
}
