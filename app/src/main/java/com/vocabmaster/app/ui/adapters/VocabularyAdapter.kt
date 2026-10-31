package com.vocabmaster.app.ui.adapters

import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.vocabmaster.app.databinding.ItemWordBinding
import com.vocabmaster.app.domain.models.Word
import java.util.Locale

fun interface OnWordActionListener {
    fun onWordAction(word: Word)
}

class VocabularyAdapter(
    private val words: List<Word>,
    private val onLearnedClick: OnWordActionListener,
    private val onDeleteClick: OnWordActionListener
) : RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper().apply { setOpenOnlyOne(true) }
    private var tts: TextToSpeech? = null

    class VocabularyViewHolder(val binding: ItemWordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        tts = TextToSpeech(recyclerView.context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

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

        holder.binding.btnSpeaker.setOnClickListener {
            tts?.speak(word.englishWord, TextToSpeech.QUEUE_FLUSH, null, word.id)
        }

        holder.binding.btnLearned.setOnClickListener {
            viewBinderHelper.closeLayout(word.id)
            onLearnedClick.onWordAction(word)
        }
        holder.binding.btnQuickLearned.setOnClickListener {
            onLearnedClick.onWordAction(word)
        }
        holder.binding.btnDelete.setOnClickListener {
            viewBinderHelper.closeLayout(word.id)
            onDeleteClick.onWordAction(word)
        }
    }

    override fun getItemCount() = words.size
}
