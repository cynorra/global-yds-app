package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.repository.ExamRepository
import kotlinx.coroutines.launch

data class ExamListItem(
    val id: String,
    val title: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val isCompleted: Boolean,
    val score: Int
)

class ExamViewModel(private val repository: ExamRepository) : ViewModel() {

    private val _uiState = MutableLiveData(ListUiState<ExamListItem>())
    val uiState: LiveData<ListUiState<ExamListItem>> get() = _uiState

    private val _mistakesCount = MutableLiveData<Int>()
    val mistakesCount: LiveData<Int> get() = _mistakesCount

    fun loadExams() {
        _uiState.value = ListUiState(isLoading = true)
        viewModelScope.launch {
            val mistakes = repository.getMistakes()
            _mistakesCount.value = mistakes.size

            val allExamsResult = repository.getAllExams()
            if (allExamsResult is Resource.Error) {
                _uiState.value = ListUiState(isLoading = false, errorMessage = allExamsResult.message)
                return@launch
            }
            val allExams = (allExamsResult as Resource.Success).data

            // If fetching the user's completed attempts fails, still show the exam list rather
            // than failing the whole screen - it just means completed exams won't be marked yet.
            val attemptByExamId = when (val completedResult = repository.getCompletedExams()) {
                is Resource.Success -> completedResult.data.associateBy { it.examId }
                is Resource.Error -> emptyMap()
            }

            val items = allExams.map { exam ->
                val attempt = attemptByExamId[exam.id]
                ExamListItem(
                    id = exam.id,
                    title = exam.title,
                    totalQuestions = exam.totalQuestions,
                    durationMinutes = exam.durationMinutes,
                    isCompleted = attempt != null,
                    score = attempt?.score ?: 0
                )
            }
            _uiState.value = ListUiState(isLoading = false, items = items)
        }
    }
}
