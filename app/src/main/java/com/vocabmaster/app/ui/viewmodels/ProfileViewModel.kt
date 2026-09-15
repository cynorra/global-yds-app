package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vocabmaster.app.data.repository.GamificationRepository
import com.vocabmaster.app.domain.Resource
import com.vocabmaster.app.domain.gamification.Achievement
import com.vocabmaster.app.domain.gamification.AchievementEngine
import com.vocabmaster.app.domain.gamification.levelForXp
import com.vocabmaster.app.domain.gamification.xpIntoCurrentLevel
import com.vocabmaster.app.domain.repository.ExamRepository
import com.vocabmaster.app.domain.repository.VocabularyRepository
import kotlinx.coroutines.launch

data class ProfileStats(
    val learnedWordsCount: Int,
    val completedExamScores: List<Int>,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val level: Int = 1,
    val xpIntoLevel: Int = 0,
    val achievements: List<Achievement> = emptyList()
)

class ProfileViewModel @JvmOverloads constructor(
    private val vocabularyRepository: VocabularyRepository,
    private val examRepository: ExamRepository,
    // Nullable with no default: defaulting to FirebaseFirestore.getInstance() would evaluate
    // eagerly at every call site (including plain-JVM unit tests with no Firebase context,
    // which crashes on Process.myPid). Production passes it explicitly (see ProfileFragment);
    // tests simply omit it and get no streak, same as an offline/logged-out user.
    private val firestore: FirebaseFirestore? = null
) : ViewModel() {

    private val _stats = MutableLiveData<ProfileStats>()
    val stats: LiveData<ProfileStats> get() = _stats

    fun loadStats() {
        viewModelScope.launch {
            val learnedWordsCount = when (val result = vocabularyRepository.getLearnedWords()) {
                is Resource.Success -> result.data.size
                is Resource.Error -> 0
            }
            val completedExamScores = when (val result = examRepository.getCompletedExams()) {
                is Resource.Success -> result.data.map { it.score }
                is Resource.Error -> emptyList()
            }
            val totalXp = completedExamScores.sum()

            // firestore == null means no real backend is wired (e.g. a plain-JVM unit test) -
            // skip FirebaseAuth too in that case, since FirebaseAuth.getInstance() itself
            // requires a live FirebaseApp and would throw with no Firebase context.
            val streak = firestore?.let { db ->
                FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
                    GamificationRepository.getStreak(db, uid)
                }
            }

            val achievements = AchievementEngine.compute(
                learnedWords = learnedWordsCount,
                completedExamScores = completedExamScores,
                currentStreak = streak?.current ?: 0,
                longestStreak = streak?.longest ?: 0
            )

            _stats.value = ProfileStats(
                learnedWordsCount = learnedWordsCount,
                completedExamScores = completedExamScores,
                currentStreak = streak?.current ?: 0,
                longestStreak = streak?.longest ?: 0,
                level = levelForXp(totalXp),
                xpIntoLevel = xpIntoCurrentLevel(totalXp),
                achievements = achievements
            )
        }
    }
}
