package com.vocabmaster.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vocabmaster.app.databinding.ItemAchievementBinding
import com.vocabmaster.app.domain.gamification.Achievement

class AchievementAdapter(private val achievements: List<Achievement>) :
    RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(val binding: ItemAchievementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        val context = holder.binding.root.context
        holder.binding.tvAchievementTitle.text = context.getString(achievement.titleRes)
        holder.binding.tvAchievementDesc.text = context.getString(achievement.descRes)
        // Locked achievements render at reduced opacity rather than being hidden entirely -
        // showing what's still to unlock is part of what makes a badge list motivating.
        val alpha = if (achievement.unlocked) 1.0f else 0.35f
        holder.binding.ivAchievementIcon.alpha = alpha
        holder.binding.tvAchievementTitle.alpha = alpha
        holder.binding.tvAchievementDesc.alpha = alpha
    }

    override fun getItemCount() = achievements.size
}
