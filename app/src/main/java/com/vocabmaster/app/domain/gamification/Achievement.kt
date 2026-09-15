package com.vocabmaster.app.domain.gamification

import com.vocabmaster.app.R

data class Achievement(
    val id: String,
    val titleRes: Int,
    val descRes: Int,
    val unlocked: Boolean
)

/** XP needed to go from level N to level N+1. Reuses exam totalScore as XP - no new counter. */
private const val LEVEL_XP_STEP = 200

fun levelForXp(xp: Int): Int = 1 + xp / LEVEL_XP_STEP

fun xpIntoCurrentLevel(xp: Int): Int = xp % LEVEL_XP_STEP

/** Pure function so achievement rules can be reasoned about (and tested) without Firestore. */
object AchievementEngine {
    fun compute(
        learnedWords: Int,
        completedExamScores: List<Int>,
        currentStreak: Int,
        longestStreak: Int
    ): List<Achievement> {
        val bestScore = completedExamScores.maxOrNull() ?: 0
        return listOf(
            Achievement("first_word", R.string.achievement_first_word_title, R.string.achievement_first_word_desc, learnedWords >= 1),
            Achievement("words_25", R.string.achievement_words_25_title, R.string.achievement_words_25_desc, learnedWords >= 25),
            Achievement("words_100", R.string.achievement_words_100_title, R.string.achievement_words_100_desc, learnedWords >= 100),
            Achievement("first_exam", R.string.achievement_first_exam_title, R.string.achievement_first_exam_desc, completedExamScores.isNotEmpty()),
            Achievement("perfect_score", R.string.achievement_perfect_score_title, R.string.achievement_perfect_score_desc, bestScore >= 100),
            Achievement("streak_3", R.string.achievement_streak_3_title, R.string.achievement_streak_3_desc, longestStreak >= 3 || currentStreak >= 3),
            Achievement("streak_7", R.string.achievement_streak_7_title, R.string.achievement_streak_7_desc, longestStreak >= 7 || currentStreak >= 7)
        )
    }
}
