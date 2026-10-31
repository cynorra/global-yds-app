package com.vocabmaster.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.vocabmaster.app.data.remote.RetrofitClient
import com.vocabmaster.app.data.repository.FirebaseExamRepositoryImpl
import com.vocabmaster.app.domain.models.Question
import com.vocabmaster.app.ui.adapters.ExamResultAdapter
import com.vocabmaster.app.ui.viewmodels.ExamTakingViewModel
import com.vocabmaster.app.util.HapticUtils
import com.vocabmaster.app.util.InAppReviewManager
import com.vocabmaster.app.util.InterstitialAdManager

class ExamTakingActivity : AppCompatActivity() {

    private lateinit var tvQuestionCounter: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvQuestionText: TextView
    private lateinit var optionButtons: List<MaterialButton>
    private lateinit var btnNext: MaterialButton
    private lateinit var groupQuiz: android.widget.LinearLayout
    private lateinit var groupResult: android.widget.LinearLayout
    private lateinit var tvScoreResult: TextView
    private lateinit var rvExamResultDetails: RecyclerView
    private lateinit var resultAdapter: ExamResultAdapter

    private lateinit var examId: String
    private var selectedButtonIndex: Int? = null

    private val viewModel: ExamTakingViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ExamTakingViewModel(FirebaseExamRepositoryImpl(FirebaseFirestore.getInstance(), RetrofitClient.api)) as T
            }
        })[ExamTakingViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_taking)

        examId = intent.getStringExtra(EXTRA_EXAM_ID) ?: run { finish(); return }
        val examTitle = intent.getStringExtra(EXTRA_EXAM_TITLE).orEmpty()
        val durationMinutes = intent.getIntExtra(EXTRA_EXAM_DURATION_MINUTES, DEFAULT_DURATION_MINUTES)

        findViewById<TextView>(R.id.tvExamTitle).text = examTitle
        tvQuestionCounter = findViewById(R.id.tvQuestionCounter)
        progressBar = findViewById(R.id.progressBar)
        tvQuestionText = findViewById(R.id.tvQuestionText)
        btnNext = findViewById(R.id.btnNext)
        groupQuiz = findViewById(R.id.groupQuiz)
        groupResult = findViewById(R.id.groupResult)
        tvScoreResult = findViewById(R.id.tvScoreResult)
        rvExamResultDetails = findViewById(R.id.rvExamResultDetails)
        
        rvExamResultDetails.layoutManager = LinearLayoutManager(this)
        resultAdapter = ExamResultAdapter()
        rvExamResultDetails.adapter = resultAdapter

        optionButtons = listOf(
            findViewById(R.id.btnOption1),
            findViewById(R.id.btnOption2),
            findViewById(R.id.btnOption3),
            findViewById(R.id.btnOption4)
        )

        optionButtons.forEachIndexed { index, button ->
            button.setOnClickListener { onOptionSelected(index) }
        }

        btnNext.setOnClickListener {
            viewModel.goToNextQuestion(examId)
        }

        InterstitialAdManager.preload(this)

        findViewById<MaterialButton>(R.id.btnShareScore).setOnClickListener {
            val score = viewModel.finalScorePercent.value ?: 0
            val shareText = getString(R.string.share_score_text, score)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app_title))
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.exam_share_score)))
        }

        findViewById<MaterialButton>(R.id.btnFinish).setOnClickListener {
            setResult(Activity.RESULT_OK)
            InterstitialAdManager.showIfReady(this) { finish() }
        }

        viewModel.currentQuestion.observe(this) { question ->
            question?.let { bindQuestion(it) }
        }

        viewModel.currentIndex.observe(this) { updateCounter() }
        viewModel.totalQuestions.observe(this) { updateCounter() }

        viewModel.finalScorePercent.observe(this) { score ->
            if (score != null) showResult(score)
        }

        viewModel.resultDetails.observe(this) { details ->
            if (details != null) {
                resultAdapter.submitList(details)
            }
        }

        val tvTimer = findViewById<TextView>(R.id.tvTimer)
        viewModel.timeRemaining.observe(this) { seconds ->
            val minutes = seconds / 60
            val secs = seconds % 60
            tvTimer.text = String.format("%02d:%02d", minutes, secs)
            if (seconds <= 10) {
                tvTimer.setTextColor(Color.RED)
            } else {
                tvTimer.setTextColor(getColor(R.color.colorPrimary))
            }
        }

        viewModel.loadQuestions(examId, examTitle, durationMinutes)
    }

    private fun bindQuestion(question: Question) {
        selectedButtonIndex = null
        btnNext.isEnabled = false
        btnNext.text = if (viewModel.isLastQuestion()) getString(R.string.exam_finish) else getString(R.string.exam_next)

        tvQuestionText.text = question.text
        optionButtons.forEachIndexed { index, button ->
            button.text = question.options.getOrNull(index).orEmpty()
            resetButtonStyle(button)
        }
    }

    private fun onOptionSelected(index: Int) {
        selectedButtonIndex = index
        viewModel.selectAnswer(index)
        btnNext.isEnabled = true
        HapticUtils.tap(optionButtons[index])
        val selectedBtn = optionButtons[index]
        selectedBtn.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).withEndAction {
            selectedBtn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start()
        }.start()
        optionButtons.forEachIndexed { i, button ->
            if (i == index) {
                button.setBackgroundColor(getColor(R.color.colorPrimary))
                button.setTextColor(Color.WHITE)
            } else {
                resetButtonStyle(button)
            }
        }
    }

    private fun resetButtonStyle(button: MaterialButton) {
        button.setBackgroundColor(Color.TRANSPARENT)
        button.setTextColor(getColor(R.color.textColorPrimary))
    }

    private fun updateCounter() {
        val index = viewModel.currentIndex.value ?: 0
        val total = viewModel.totalQuestions.value ?: 0
        if (total == 0) return
        tvQuestionCounter.text = getString(R.string.exam_question_counter, index + 1, total)
        progressBar.max = total
        val targetProgress = index + 1
        android.animation.ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, targetProgress)
            .setDuration(300)
            .start()
    }

    private fun showResult(scorePercent: Int) {
        groupQuiz.visibility = android.view.View.GONE
        groupResult.visibility = android.view.View.VISIBLE
        groupResult.alpha = 0f
        groupResult.translationY = 40f
        groupResult.animate().alpha(1f).translationY(0f).setDuration(400).setInterpolator(android.view.animation.DecelerateInterpolator()).start()
        tvScoreResult.text = getString(R.string.exam_score_format, scorePercent)

        // Only celebrate an actual pass; confetti on a 0-59% result reads as mocking rather
        // than congratulating.
        if (scorePercent >= PASSING_SCORE_PERCENT) {
            HapticUtils.success(tvScoreResult)
            InAppReviewManager.requestReviewIfEligible(this)
            com.github.jinatonic.confetti.CommonConfetti.rainingConfetti(
                findViewById<ViewGroup>(android.R.id.content),
                intArrayOf(
                    getColor(R.color.colorPrimary),
                    getColor(R.color.colorAccent),
                    getColor(R.color.colorWarning),
                    Color.parseColor("#EC4899")
                )
            ).oneShot()
        }
    }

    companion object {
        private const val EXTRA_EXAM_ID = "extra_exam_id"
        private const val EXTRA_EXAM_TITLE = "extra_exam_title"
        private const val EXTRA_EXAM_DURATION_MINUTES = "extra_exam_duration_minutes"
        private const val DEFAULT_DURATION_MINUTES = 15
        private const val PASSING_SCORE_PERCENT = 60

        @JvmStatic
        @JvmOverloads
        fun createIntent(
            context: Context,
            examId: String,
            examTitle: String,
            durationMinutes: Int = DEFAULT_DURATION_MINUTES
        ): Intent {
            val intent = Intent(context, ExamTakingActivity::class.java)
            intent.putExtra(EXTRA_EXAM_ID, examId)
            intent.putExtra(EXTRA_EXAM_TITLE, examTitle)
            intent.putExtra(EXTRA_EXAM_DURATION_MINUTES, durationMinutes)
            return intent
        }
    }
}
