package com.vocabmaster.app.ui.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vocabmaster.app.R
import com.vocabmaster.app.domain.models.LeaderboardUser

class LeaderboardAdapter(
    private var users: List<LeaderboardUser> = emptyList()
) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    fun submitList(newUsers: List<LeaderboardUser>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user, position + 1)
    }

    override fun getItemCount() = users.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvRank: TextView = view.findViewById(R.id.tvRank)
        private val tvUsername: TextView = view.findViewById(R.id.tvUsername)
        private val tvScore: TextView = view.findViewById(R.id.tvScore)

        fun bind(user: LeaderboardUser, rank: Int) {
            tvRank.text = rank.toString()
            tvUsername.text = user.username
            tvScore.text = "Score: ${user.totalScore}"

            val background = tvRank.background as? GradientDrawable
            when (rank) {
                1 -> background?.setColor(Color.parseColor("#FFD700")) // Gold
                2 -> background?.setColor(Color.parseColor("#C0C0C0")) // Silver
                3 -> background?.setColor(Color.parseColor("#CD7F32")) // Bronze
                else -> background?.setColor(itemView.context.getColor(R.color.colorPrimary))
            }
        }
    }
}
