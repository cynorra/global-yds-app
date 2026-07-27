package com.example.globalyds.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globalyds.domain.models.Exam
import com.example.globalyds.domain.repository.ExamRepository
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
