package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.models.ReadingPassage
import com.vocabmaster.app.domain.repository.ReadingRepository
import kotlinx.coroutines.launch

class ReadingViewModel(private val repository: ReadingRepository) : ViewModel() {

    private val _uiState = MutableLiveData(ListUiState<ReadingPassage>())
    val uiState: LiveData<ListUiState<ReadingPassage>> get() = _uiState

    fun loadPassages() {
        _uiState.value = ListUiState(isLoading = true)
        viewModelScope.launch {
            _uiState.value = when (val result = repository.getPassages()) {
                is Resource.Success -> ListUiState(isLoading = false, items = result.data)
                is Resource.Error -> ListUiState(isLoading = false, errorMessage = result.message)
            }
        }
    }
}
