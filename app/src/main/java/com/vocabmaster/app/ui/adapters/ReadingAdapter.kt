package com.vocabmaster.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vocabmaster.app.databinding.ItemReadingPassageBinding
import com.vocabmaster.app.domain.models.ReadingPassage

class ReadingAdapter(private val passages: List<ReadingPassage>) :
    RecyclerView.Adapter<ReadingAdapter.ReadingViewHolder>() {

    class ReadingViewHolder(val binding: ItemReadingPassageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingViewHolder {
        val binding = ItemReadingPassageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReadingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReadingViewHolder, position: Int) {
        val passage = passages[position]
        holder.binding.tvTitle.text = passage.title
        holder.binding.tvBody.text = passage.bodyText
    }

    override fun getItemCount() = passages.size
}
