package com.vocabmaster.app.ui.adapters

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vocabmaster.app.databinding.ItemGrammarTopicBinding
import com.vocabmaster.app.domain.models.GrammarTopic

class GrammarAdapter(private val topics: List<GrammarTopic>) :
    RecyclerView.Adapter<GrammarAdapter.GrammarViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    class GrammarViewHolder(val binding: ItemGrammarTopicBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrammarViewHolder {
        val binding = ItemGrammarTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GrammarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GrammarViewHolder, position: Int) {
        val topic = topics[position]
        holder.binding.tvTitle.text = topic.title
        holder.binding.tvExplanation.text = topic.explanation
        holder.binding.tvExample.text = topic.exampleSentence

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

    private fun applyExpandedState(holder: GrammarViewHolder, expanded: Boolean) {
        holder.binding.tvExplanation.maxLines = if (expanded) Int.MAX_VALUE else 2
        holder.binding.tvExample.visibility = if (expanded) View.VISIBLE else View.GONE
        holder.binding.ivExpandIcon.rotation = if (expanded) 180f else 0f
    }

    override fun getItemCount() = topics.size
}
