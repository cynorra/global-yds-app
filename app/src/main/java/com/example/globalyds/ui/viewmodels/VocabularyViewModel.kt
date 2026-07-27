package com.example.globalyds.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globalyds.domain.models.Word
import com.example.globalyds.domain.repository.VocabularyRepository
import kotlinx.coroutines.launch

class VocabularyViewModel(private val repository: VocabularyRepository) : ViewModel() {

    private val _words = MutableLiveData<List<Word>>()
    val words: LiveData<List<Word>> get() = _words

    fun loadWords() {
        viewModelScope.launch {
            // Placeholder: Calling the repository to get words
            _words.value = repository.getDailyWords()
        }
    }
}
