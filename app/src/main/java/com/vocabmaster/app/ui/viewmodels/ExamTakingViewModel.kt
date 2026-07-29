package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaster.app.domain.models.Question
import com.vocabmaster.app.domain.repository.ExamRepository
import kotlinx.coroutines.launch

class ExamTakingViewModel(private val repository: ExamRepository) : ViewModel() {

    private var questions: List<Question> = emptyList()
    private val selectedAnswers = mutableMapOf<Int, Int>()

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> get() = _currentIndex

    private val _currentQuestion = MutableLiveData<Question?>()
    val currentQuestion: LiveData<Question?> get() = _currentQuestion

    private val _totalQuestions = MutableLiveData(0)
    val totalQuestions: LiveData<Int> get() = _totalQuestions

    private val _finalScorePercent = MutableLiveData<Int?>()
    val finalScorePercent: LiveData<Int?> get() = _finalScorePercent

    fun loadQuestions(examId: String) {
        viewModelScope.launch {
            questions = repository.getExamQuestions(examId)
            _totalQuestions.value = questions.size
            _currentIndex.value = 0
            _currentQuestion.value = questions.firstOrNull()
        }
    }

    fun selectAnswer(optionIndex: Int) {
        selectedAnswers[_currentIndex.value ?: 0] = optionIndex
    }

    fun selectedAnswerForCurrentQuestion(): Int? = selectedAnswers[_currentIndex.value ?: 0]

    fun isLastQuestion(): Boolean = (_currentIndex.value ?: 0) == questions.size - 1

    fun goToNextQuestion(examId: String) {
        val nextIndex = (_currentIndex.value ?: 0) + 1
        if (nextIndex < questions.size) {
            _currentIndex.value = nextIndex
            _currentQuestion.value = questions[nextIndex]
        } else {
            finishExam(examId)
        }
    }

    private fun finishExam(examId: String) {
        val correctCount = questions.indices.count { i -> selectedAnswers[i] == questions[i].correctOptionIndex }
        val scorePercent = if (questions.isNotEmpty()) (correctCount * 100) / questions.size else 0
        viewModelScope.launch {
            repository.submitExamResult(examId, scorePercent)
            _finalScorePercent.value = scorePercent
        }
    }
}
