package com.vocabmaster.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vocabmaster.app.databinding.ItemExamBinding
import com.vocabmaster.app.domain.models.Exam

class ExamAdapter(private val exams: List<Exam>) :
    RecyclerView.Adapter<ExamAdapter.ExamViewHolder>() {

    class ExamViewHolder(val binding: ItemExamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val binding = ItemExamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        val exam = exams[position]
        holder.binding.tvTitle.text = exam.title
        holder.binding.tvScore.text = "Score: ${exam.score}"
        holder.binding.tvDetails.text = "${exam.totalQuestions} Questions • ${exam.durationMinutes} Minutes"
    }

    override fun getItemCount() = exams.size
}
