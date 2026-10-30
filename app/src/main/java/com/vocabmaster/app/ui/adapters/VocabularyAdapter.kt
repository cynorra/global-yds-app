package com.vocabmaster.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.vocabmaster.app.databinding.ItemWordBinding
import com.vocabmaster.app.domain.models.Word

fun interface OnWordActionListener {
    fun onWordAction(word: Word)
}

class VocabularyAdapter(
    private val words: List<Word>,
    private val onLearnedClick: OnWordActionListener,
    private val onDeleteClick: OnWordActionListener
) : RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper().apply { setOpenOnlyOne(true) }

    class VocabularyViewHolder(val binding: ItemWordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabularyViewHolder {
        val binding = ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VocabularyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VocabularyViewHolder, position: Int) {
        val word = words[position]
        val examTypeObj = com.vocabmaster.app.domain.models.ExamType.fromCode(word.targetExam)
        if (word.targetExam == "ALL") {
            holder.binding.tvTargetExamBadge.text = "🌍 Genel"
        } else {
            holder.binding.tvTargetExamBadge.text = "${examTypeObj.flag} ${examTypeObj.displayName}"
        }

        holder.binding.tvEnglishWord.text = word.englishWord
        holder.binding.tvTurkishMeaning.text = word.turkishMeaning
        holder.binding.tvExample.text = word.exampleSentence

        viewBinderHelper.bind(holder.binding.swipeRevealLayout, word.id)

        holder.binding.btnLearned.setOnClickListener {
            viewBinderHelper.closeLayout(word.id)
            onLearnedClick.onWordAction(word)
        }
        holder.binding.btnDelete.setOnClickListener {
            viewBinderHelper.closeLayout(word.id)
            onDeleteClick.onWordAction(word)
        }
    }

    override fun getItemCount() = words.size
}
