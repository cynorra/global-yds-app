package com.vocabmaster.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vocabmaster.app.databinding.ItemExamBinding
import com.vocabmaster.app.domain.models.ExamType
import com.vocabmaster.app.ui.viewmodels.ExamListItem

fun interface OnExamClickListener {
    fun onExamClick(exam: ExamListItem)
}

class ExamAdapter(
    private val exams: List<ExamListItem>,
    private val onExamClick: OnExamClickListener
) : RecyclerView.Adapter<ExamAdapter.ExamViewHolder>() {

    class ExamViewHolder(val binding: ItemExamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val binding = ItemExamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        val exam = exams[position]
        val examTypeObj = ExamType.fromCode(exam.examType)

        val badgeText = if (exam.examType == "ALL") {
            "🌍 Genel • ${exam.levelBadge}"
        } else {
            "${examTypeObj.flag} ${examTypeObj.displayName} • ${exam.levelBadge}"
        }
        holder.binding.tvExamBadge.text = badgeText

        holder.binding.tvTitle.text = exam.title
        holder.binding.tvDetails.text = "${exam.totalQuestions} Soru • ${exam.durationMinutes} Dk"

        if (exam.isCompleted) {
            holder.binding.tvScore.visibility = View.VISIBLE
            holder.binding.tvScore.text = "✓ %${exam.score}"
            holder.itemView.isClickable = false
            holder.itemView.setOnClickListener(null)
        } else {
            holder.binding.tvScore.visibility = View.GONE
            holder.itemView.isClickable = true
            holder.itemView.setOnClickListener { onExamClick.onExamClick(exam) }
        }
    }

    override fun getItemCount() = exams.size
}
