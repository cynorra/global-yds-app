package com.vocabmaster.app.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vocabmaster.app.R
import com.vocabmaster.app.ui.viewmodels.ExamResultDetail

class ExamResultAdapter(
    private var details: List<ExamResultDetail> = emptyList()
) : RecyclerView.Adapter<ExamResultAdapter.ViewHolder>() {

    fun submitList(newDetails: List<ExamResultDetail>) {
        details = newDetails
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exam_result_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(details[position])
    }

    override fun getItemCount() = details.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvQuestion: TextView = view.findViewById(R.id.tvQuestion)
        private val tvUserAnswer: TextView = view.findViewById(R.id.tvUserAnswer)
        private val tvCorrectAnswer: TextView = view.findViewById(R.id.tvCorrectAnswer)

        fun bind(detail: ExamResultDetail) {
            val q = detail.question
            tvQuestion.text = q.text

            val selectedOpt = detail.selectedOptionIndex
            val correctOpt = q.correctOptionIndex

            val context = tvUserAnswer.context

            if (selectedOpt == null) {
                tvUserAnswer.text = context.getString(R.string.result_blank)
                tvUserAnswer.setTextColor(Color.parseColor("#9CA3AF"))
                tvCorrectAnswer.visibility = View.VISIBLE
                tvCorrectAnswer.text = context.getString(R.string.result_correct_answer, q.options.getOrNull(correctOpt) ?: "")
            } else if (selectedOpt == correctOpt) {
                tvUserAnswer.text = context.getString(R.string.result_your_answer, q.options.getOrNull(selectedOpt) ?: "")
                tvUserAnswer.setTextColor(Color.parseColor("#10B981")) // Green
                tvCorrectAnswer.visibility = View.GONE
            } else {
                tvUserAnswer.text = context.getString(R.string.result_your_answer, q.options.getOrNull(selectedOpt) ?: "")
                tvUserAnswer.setTextColor(Color.parseColor("#EF4444")) // Red
                tvCorrectAnswer.visibility = View.VISIBLE
                tvCorrectAnswer.text = context.getString(R.string.result_correct_answer, q.options.getOrNull(correctOpt) ?: "")
            }
        }
    }
}
