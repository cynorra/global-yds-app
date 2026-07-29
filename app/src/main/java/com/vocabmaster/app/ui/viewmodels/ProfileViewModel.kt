package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            val learnedWordsCount = vocabularyRepository.getLearnedWords().size
            val completedExamScores = examRepository.getCompletedExams().map { it.score }
            _stats.value = ProfileStats(learnedWordsCount, completedExamScores)
        }
    }
}
