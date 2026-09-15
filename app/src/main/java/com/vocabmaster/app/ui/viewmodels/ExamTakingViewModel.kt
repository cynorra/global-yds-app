package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaster.app.domain.models.Question
import com.vocabmaster.app.domain.repository.ExamRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job

class ExamTakingViewModel(private val repository: ExamRepository) : ViewModel() {

    private var questions: List<Question> = emptyList()
    private val selectedAnswers = mutableMapOf<Int, Int>()
    private var examTitle: String = ""
    private var currentExamId: String = ""

    private var timerJob: Job? = null

    private val _timeRemaining = MutableLiveData<Int>()
    val timeRemaining: LiveData<Int> get() = _timeRemaining

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> get() = _currentIndex

    private val _currentQuestion = MutableLiveData<Question?>()
    val currentQuestion: LiveData<Question?> get() = _currentQuestion

    private val _totalQuestions = MutableLiveData(0)
    val totalQuestions: LiveData<Int> get() = _totalQuestions

    private val _finalScorePercent = MutableLiveData<Int?>()
    val finalScorePercent: LiveData<Int?> get() = _finalScorePercent

    private val _resultDetails = MutableLiveData<List<ExamResultDetail>>()
    val resultDetails: LiveData<List<ExamResultDetail>> get() = _resultDetails

    fun loadQuestions(examId: String, examTitle: String, durationMinutes: Int) {
        this.examTitle = examTitle
        this.currentExamId = examId
        viewModelScope.launch {
            questions = repository.getExamQuestions(examId)
            _totalQuestions.value = questions.size
            _currentIndex.value = 0
            _currentQuestion.value = questions.firstOrNull()
            startTimer(durationMinutes * 60)
        }
    }

    private fun startTimer(durationSeconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (i in durationSeconds downTo 0) {
                _timeRemaining.value = i
                if (i == 0) {
                    finishExam(currentExamId)
                }
                delay(1000)
            }
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
        timerJob?.cancel()
        val correctCount = questions.indices.count { i -> selectedAnswers[i] == questions[i].correctOptionIndex }
        val scorePercent = if (questions.isNotEmpty()) (correctCount * 100) / questions.size else 0
        
        val details = questions.mapIndexed { index, question -> 
            ExamResultDetail(question, selectedAnswers[index]) 
        }

        val mistakes = questions.filterIndexed { index, question ->
            selectedAnswers[index] != question.correctOptionIndex
        }

        val corrects = questions.filterIndexed { index, question ->
            selectedAnswers[index] == question.correctOptionIndex
        }
        
        viewModelScope.launch {
            if (examId == "MISTAKES") {
                corrects.forEach { q -> repository.removeMistake(q.id) }
            } else {
                repository.submitExamResult(examId, examTitle, scorePercent, mistakes)
            }
            _resultDetails.value = details
            _finalScorePercent.value = scorePercent
        }
    }
}

data class ExamResultDetail(
    val question: Question,
    val selectedOptionIndex: Int?
)
