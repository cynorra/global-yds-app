package com.vocabmaster.app.domain

/**
 * Distinguishes "the request failed" from "the request succeeded with an empty result" so the UI
 * can show a retry-able error state instead of silently rendering an empty list on network/DB
 * failures.
 */
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String?) : Resource<Nothing>()
}
