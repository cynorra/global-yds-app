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

// scorePercent == null means the topic hasn't been attempted at all yet (surfaced ahead of a low
// score, since zero data on a topic is a bigger gap than a low-but-attempted one).
data class WeakAreaInsight(val topicTitle: String, val scorePercent: Int?)

class ExamViewModel(private val repository: ExamRepository) : ViewModel() {

    private val _uiState = MutableLiveData(ListUiState<ExamListItem>())
    val uiState: LiveData<ListUiState<ExamListItem>> get() = _uiState

    private val _mistakesCount = MutableLiveData<Int>()
    val mistakesCount: LiveData<Int> get() = _mistakesCount

    private val _weakAreaInsight = MutableLiveData<WeakAreaInsight?>()
    val weakAreaInsight: LiveData<WeakAreaInsight?> get() = _weakAreaInsight

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
            _weakAreaInsight.value = computeWeakAreaInsight(items)
        }
    }

    // Each of the 5 seed exams IS a fixed grammar topic (its own title), so the exam list
    // doubles as a topic list. The daily exam is excluded - its pool is a random mix, not a
    // single topic, so a low/missing score there doesn't point at anything specific to practice.
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
