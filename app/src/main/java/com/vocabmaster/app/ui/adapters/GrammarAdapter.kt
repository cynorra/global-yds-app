package com.vocabmaster.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vocabmaster.app.databinding.ItemGrammarTopicBinding
import com.vocabmaster.app.domain.models.GrammarTopic

class GrammarAdapter(private val topics: List<GrammarTopic>) :
    RecyclerView.Adapter<GrammarAdapter.GrammarViewHolder>() {

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
    }

    override fun getItemCount() = topics.size
}
