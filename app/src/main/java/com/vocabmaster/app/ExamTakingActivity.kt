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

        findViewById<MaterialButton>(R.id.btnFinish).setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
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

        viewModel.loadQuestions(examId, examTitle)
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
        optionButtons.forEachIndexed { i, button ->
            if (i == index) {
                button.setBackgroundColor(Color.parseColor("#4F46E5"))
                button.setTextColor(Color.WHITE)
            } else {
                resetButtonStyle(button)
            }
        }
    }

    private fun resetButtonStyle(button: MaterialButton) {
        button.setBackgroundColor(Color.TRANSPARENT)
        button.setTextColor(Color.parseColor("#111827"))
    }

    private fun updateCounter() {
        val index = viewModel.currentIndex.value ?: 0
        val total = viewModel.totalQuestions.value ?: 0
        if (total == 0) return
        tvQuestionCounter.text = getString(R.string.exam_question_counter, index + 1, total)
        progressBar.max = total
        progressBar.progress = index + 1
    }

    private fun showResult(scorePercent: Int) {
        groupQuiz.visibility = android.view.View.GONE
        groupResult.visibility = android.view.View.VISIBLE
        tvScoreResult.text = getString(R.string.exam_score_format, scorePercent)

        com.github.jinatonic.confetti.CommonConfetti.rainingConfetti(
            findViewById<ViewGroup>(android.R.id.content),
            intArrayOf(
                Color.parseColor("#4F46E5"),
                Color.parseColor("#10B981"),
                Color.parseColor("#F59E0B"),
                Color.parseColor("#EC4899")
            )
        ).oneShot()
    }

    companion object {
        private const val EXTRA_EXAM_ID = "extra_exam_id"
        private const val EXTRA_EXAM_TITLE = "extra_exam_title"

        @JvmStatic
        fun createIntent(context: Context, examId: String, examTitle: String): Intent {
            val intent = Intent(context, ExamTakingActivity::class.java)
            intent.putExtra(EXTRA_EXAM_ID, examId)
            intent.putExtra(EXTRA_EXAM_TITLE, examTitle)
            return intent
        }
    }
}
