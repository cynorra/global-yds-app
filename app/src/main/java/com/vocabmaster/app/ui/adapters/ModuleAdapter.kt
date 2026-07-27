package com.vocabmaster.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vocabmaster.app.databinding.ItemModuleBinding
import com.vocabmaster.app.domain.models.Module

class ModuleAdapter(private val modules: List<Module>) :
    RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder>() {

    class ModuleViewHolder(val binding: ItemModuleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = modules[position]
        holder.binding.tvTitle.text = module.title
        holder.binding.tvDescription.text = module.description
        holder.binding.ivIcon.setImageResource(module.iconResId)
    }

    override fun getItemCount() = modules.size
}
