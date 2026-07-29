package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaster.app.domain.models.ReadingPassage
import com.vocabmaster.app.domain.repository.ReadingRepository
import kotlinx.coroutines.launch

class ReadingViewModel(private val repository: ReadingRepository) : ViewModel() {

    private val _passages = MutableLiveData<List<ReadingPassage>>()
    val passages: LiveData<List<ReadingPassage>> get() = _passages

    fun loadPassages() {
        viewModelScope.launch {
            _passages.value = repository.getPassages()
        }
    }
}
