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
    val score: Int,
    val examType: String = "ALL",
    val levelBadge: String = "B2-C1",
    val difficulty: String = "Medium"
)

data class WeakAreaInsight(val topicTitle: String, val scorePercent: Int?)

class ExamViewModel(private val repository: ExamRepository) : ViewModel() {

    private val _uiState = MutableLiveData(ListUiState<ExamListItem>())
    val uiState: LiveData<ListUiState<ExamListItem>> get() = _uiState

    private val _mistakesCount = MutableLiveData<Int>()
    val mistakesCount: LiveData<Int> get() = _mistakesCount

    private val _weakAreaInsight = MutableLiveData<WeakAreaInsight?>()
    val weakAreaInsight: LiveData<WeakAreaInsight?> get() = _weakAreaInsight

    private var allCachedItems: List<ExamListItem> = emptyList()
    private var currentFilterExamType: String = "ALL"

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

            val attemptByExamId = when (val completedResult = repository.getCompletedExams()) {
                is Resource.Success -> completedResult.data.associateBy { it.examId }
                is Resource.Error -> emptyMap()
            }

            allCachedItems = allExams.map { exam ->
                val attempt = attemptByExamId[exam.id]
                ExamListItem(
                    id = exam.id,
                    title = exam.title,
                    totalQuestions = exam.totalQuestions,
                    durationMinutes = exam.durationMinutes,
                    isCompleted = attempt != null,
                    score = attempt?.score ?: 0,
                    examType = exam.examType,
                    levelBadge = exam.levelBadge,
                    difficulty = exam.difficulty
                )
            }
            applyCurrentFilter()
            _weakAreaInsight.value = computeWeakAreaInsight(allCachedItems)
        }
    }

    fun filterByExamType(examType: String) {
        currentFilterExamType = examType
        applyCurrentFilter()
    }

    private fun applyCurrentFilter() {
        val filtered = if (currentFilterExamType.equals("ALL", ignoreCase = true)) {
            allCachedItems
        } else {
            allCachedItems.filter { it.examType.equals(currentFilterExamType, ignoreCase = true) }
        }
        _uiState.value = ListUiState(isLoading = false, items = filtered)
    }

    private fun computeWeakAreaInsight(items: List<ExamListItem>): WeakAreaInsight? {
        val topicItems = items.filterNot { it.id.startsWith("exam-daily") }
        val notAttempted = topicItems.firstOrNull { !it.isCompleted }
        if (notAttempted != null) return WeakAreaInsight(notAttempted.title, null)

        return topicItems.filter { it.isCompleted }
            .minByOrNull { it.score }
            ?.takeIf { it.score < 100 }
            ?.let { WeakAreaInsight(it.title, it.score) }
    }
}
