package com.vocabmaster.app.ui.viewmodels

/**
 * Shared list-screen state (Grammar/Reading/Vocabulary/Exam) so every screen renders loading,
 * error+retry, and genuinely-empty states consistently instead of each fragment inventing its
 * own (or, previously, not having one at all).
 */
data class ListUiState<T>(
    val isLoading: Boolean = true,
    val items: List<T> = emptyList(),
    val errorMessage: String? = null
)
