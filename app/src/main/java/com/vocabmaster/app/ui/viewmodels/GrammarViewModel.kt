package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaster.app.domain.models.GrammarTopic
import com.vocabmaster.app.domain.repository.GrammarRepository
import kotlinx.coroutines.launch

class GrammarViewModel(private val repository: GrammarRepository) : ViewModel() {

    private val _topics = MutableLiveData<List<GrammarTopic>>()
    val topics: LiveData<List<GrammarTopic>> get() = _topics

    fun loadTopics() {
        viewModelScope.launch {
            _topics.value = repository.getTopics()
        }
    }
}
