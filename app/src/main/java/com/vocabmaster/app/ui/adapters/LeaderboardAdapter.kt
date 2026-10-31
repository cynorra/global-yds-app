package com.vocabmaster.app.ui.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
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
        private val cardView: MaterialCardView? = view as? MaterialCardView
        private val tvRank: TextView = view.findViewById(R.id.tvRank)
        private val tvUsername: TextView = view.findViewById(R.id.tvUsername)
        private val tvScore: TextView = view.findViewById(R.id.tvScore)

        fun bind(user: LeaderboardUser, rank: Int) {
            tvRank.text = rank.toString()

            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            val isCurrentUser = (currentUid != null && user.id == currentUid)

            if (isCurrentUser) {
                tvUsername.text = "${user.username} ⭐️"
                cardView?.strokeWidth = 4
                cardView?.strokeColor = itemView.context.getColor(R.color.colorPrimary)
            } else {
                tvUsername.text = user.username
                cardView?.strokeWidth = 1
                cardView?.strokeColor = itemView.context.getColor(R.color.dividerColor)
            }

            tvScore.text = "${user.totalScore} XP"

            val background = tvRank.background?.mutate() as? GradientDrawable
            when (rank) {
                1 -> background?.setColor(Color.parseColor("#FFD700")) // Gold
                2 -> background?.setColor(Color.parseColor("#C0C0C0")) // Silver
                3 -> background?.setColor(Color.parseColor("#CD7F32")) // Bronze
                else -> background?.setColor(itemView.context.getColor(R.color.colorPrimary))
            }
        }
    }
}
