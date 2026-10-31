package com.vocabmaster.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vocabmaster.app.R
import com.vocabmaster.app.ui.adapters.LeaderboardAdapter
import com.vocabmaster.app.ui.viewmodels.LeaderboardViewModel

class LeaderboardFragment : Fragment() {

    private val viewModel: LeaderboardViewModel by viewModels()
    private lateinit var adapter: LeaderboardAdapter
    private lateinit var rvLeaderboard: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvLeaderboard = view.findViewById(R.id.rvLeaderboard)
        progressBar = view.findViewById(R.id.progressBar)

        rvLeaderboard.layoutManager = LinearLayoutManager(requireContext())
        adapter = LeaderboardAdapter()
        rvLeaderboard.adapter = adapter
        rvLeaderboard.itemAnimator = jp.wasabeef.recyclerview.animators.SlideInUpAnimator(android.view.animation.OvershootInterpolator(1f))

        viewModel.users.observe(viewLifecycleOwner) { users ->
            adapter.submitList(users)
            // Restore animation layout for newly added items
            rvLeaderboard.scheduleLayoutAnimation()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Snackbar.make(view, R.string.error_generic_subtitle, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_retry) { viewModel.fetchLeaderboard() }
                    .show()
                viewModel.errorShown()
            }
        }

        viewModel.fetchLeaderboard()
    }
}
