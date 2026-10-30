package com.vocabmaster.app.ui.adapters

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vocabmaster.app.databinding.ItemReadingPassageBinding
import com.vocabmaster.app.domain.models.ReadingPassage

class ReadingAdapter(private val passages: List<ReadingPassage>) :
    RecyclerView.Adapter<ReadingAdapter.ReadingViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    class ReadingViewHolder(val binding: ItemReadingPassageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingViewHolder {
        val binding = ItemReadingPassageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReadingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReadingViewHolder, position: Int) {
        val passage = passages[position]
        holder.binding.tvTitle.text = passage.title
        holder.binding.tvBody.text = passage.bodyText

        val flag = when (passage.targetExam.uppercase()) {
            "TOEIC" -> "🇯🇵 TOEIC"
            "IELTS" -> "🇬🇧 IELTS"
            "TOEFL" -> "🇺🇸 TOEFL"
            "CAMBRIDGE" -> "🇪🇺 Cambridge"
            "SAT" -> "🎓 SAT"
            "PTE" -> "⚡ PTE"
            "DET" -> "🦉 DET"
            "GRE" -> "🏛️ GRE"
            "EIKEN" -> "🌸 EIKEN"
            "YDS" -> "🇹🇷 YDS"
            else -> "🌍 Global"
        }
        holder.binding.tvPassageBadge.text = "$flag • ${passage.levelBadge}"

        applyExpandedState(holder, expandedPositions.contains(position))

        holder.binding.root.setOnClickListener {
            TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
            val nowExpanded = if (expandedPositions.remove(position)) {
                false
            } else {
                expandedPositions.add(position)
                true
            }
            applyExpandedState(holder, nowExpanded)
        }
    }

    private fun applyExpandedState(holder: ReadingViewHolder, expanded: Boolean) {
        holder.binding.tvBody.maxLines = if (expanded) Int.MAX_VALUE else 3
        holder.binding.ivExpandIcon.rotation = if (expanded) 180f else 0f
    }

    override fun getItemCount() = passages.size
}
