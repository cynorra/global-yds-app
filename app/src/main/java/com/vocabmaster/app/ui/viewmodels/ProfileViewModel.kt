package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.repository.ExamRepository
import com.vocabmaster.app.domain.repository.VocabularyRepository
import kotlinx.coroutines.launch

data class ProfileStats(
    val learnedWordsCount: Int,
    val completedExamScores: List<Int>
)

class ProfileViewModel(
    private val vocabularyRepository: VocabularyRepository,
    private val examRepository: ExamRepository
) : ViewModel() {

    private val _stats = MutableLiveData<ProfileStats>()
    val stats: LiveData<ProfileStats> get() = _stats

    fun loadStats() {
        viewModelScope.launch {
            val learnedWordsCount = when (val result = vocabularyRepository.getLearnedWords()) {
                is Resource.Success -> result.data.size
                is Resource.Error -> 0
            }
            val completedExamScores = when (val result = examRepository.getCompletedExams()) {
                is Resource.Success -> result.data.map { it.score }
                is Resource.Error -> emptyList()
            }
            _stats.value = ProfileStats(learnedWordsCount, completedExamScores)
        }
    }
}
