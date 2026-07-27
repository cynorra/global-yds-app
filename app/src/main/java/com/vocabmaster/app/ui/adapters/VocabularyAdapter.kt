package com.vocabmaster.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vocabmaster.app.databinding.ItemWordBinding
import com.vocabmaster.app.domain.models.Word

class VocabularyAdapter(private val words: List<Word>) :
    RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder>() {

    class VocabularyViewHolder(val binding: ItemWordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabularyViewHolder {
        val binding = ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VocabularyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VocabularyViewHolder, position: Int) {
        val word = words[position]
        holder.binding.tvEnglishWord.text = word.englishWord
        holder.binding.tvTurkishMeaning.text = word.turkishMeaning
        holder.binding.tvExample.text = word.exampleSentence
    }

    override fun getItemCount() = words.size
}
