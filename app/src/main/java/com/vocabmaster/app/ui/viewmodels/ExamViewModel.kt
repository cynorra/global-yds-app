package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaster.app.domain.models.Exam
import com.vocabmaster.app.domain.repository.ExamRepository
import kotlinx.coroutines.launch

class ExamViewModel(private val repository: ExamRepository) : ViewModel() {

    private val _exams = MutableLiveData<List<Exam>>()
    val exams: LiveData<List<Exam>> get() = _exams

    fun loadExams() {
        viewModelScope.launch {
            _exams.value = repository.getAvailableExams()
        }
    }
}
