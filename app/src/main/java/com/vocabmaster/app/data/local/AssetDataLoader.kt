package com.vocabmaster.app.data.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vocabmaster.app.GlobalYdsApp
import com.vocabmaster.app.domain.models.Exam
import com.vocabmaster.app.domain.models.Question
import java.io.InputStreamReader

data class SeedExamJson(
    val id: String = "",
    val title: String = "",
    val totalQuestions: Int = 0,
    val durationMinutes: Int = 15,
    val examType: String = "ALL",
    val levelBadge: String = "B2",
    val difficulty: String = "Medium",
    val questions: List<Question> = emptyList()
) {
    fun toDomain(): Exam = Exam(
        id = id,
        title = title,
        totalQuestions = if (questions.isNotEmpty()) questions.size else totalQuestions,
        durationMinutes = durationMinutes,
        examType = examType,
        levelBadge = levelBadge,
        difficulty = difficulty
    )
}

object AssetDataLoader {
    private const val TAG = "AssetDataLoader"
    private val gson = Gson()
    private var cachedWords: List<WordEntity>? = null
    private var cachedExams: List<SeedExamJson>? = null

    fun loadWords(context: Context? = null): List<WordEntity> {
        cachedWords?.let { return it }
        val ctx = context ?: try { GlobalYdsApp.instance } catch (e: Exception) { null } ?: return emptyList()
        return try {
            ctx.assets.open("words_bank.json").use { stream ->
                InputStreamReader(stream, Charsets.UTF_8).use { reader ->
                    val type = object : TypeToken<List<WordEntity>>() {}.type
                    val words: List<WordEntity> = gson.fromJson(reader, type)
                    cachedWords = words
                    Log.d(TAG, "Loaded ${words.size} words from assets")
                    words
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load words from assets", e)
            emptyList()
        }
    }

    fun loadExams(context: Context? = null): List<SeedExamJson> {
        cachedExams?.let { return it }
        val ctx = context ?: try { GlobalYdsApp.instance } catch (e: Exception) { null } ?: return emptyList()
        return try {
            ctx.assets.open("exams_bank.json").use { stream ->
                InputStreamReader(stream, Charsets.UTF_8).use { reader ->
                    val type = object : TypeToken<List<SeedExamJson>>() {}.type
                    val exams: List<SeedExamJson> = gson.fromJson(reader, type)
                    cachedExams = exams
                    Log.d(TAG, "Loaded ${exams.size} exams from assets")
                    exams
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load exams from assets", e)
            emptyList()
        }
    }
}

