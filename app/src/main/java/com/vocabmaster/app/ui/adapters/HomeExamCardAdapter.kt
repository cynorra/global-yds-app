package com.vocabmaster.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vocabmaster.app.databinding.ItemHomeExamCardBinding
import com.vocabmaster.app.domain.models.ExamType

data class HomeExamItem(
    val examType: ExamType,
    val candidatesText: String
)

fun interface OnHomeExamClickListener {
    fun onHomeExamClick(examType: ExamType)
}

class HomeExamCardAdapter(
    private val items: List<HomeExamItem>,
    private val listener: OnHomeExamClickListener
) : RecyclerView.Adapter<HomeExamCardAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHomeExamCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeExamCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val exam = item.examType
        holder.binding.tvExamFlag.text = exam.flag
        holder.binding.tvCandidatesBadge.visibility = android.view.View.GONE
        holder.binding.tvExamName.text = exam.displayName
        holder.binding.tvExamDesc.text = exam.description

        holder.itemView.setOnClickListener {
            listener.onHomeExamClick(exam)
        }
    }

    override fun getItemCount() = items.size
}

