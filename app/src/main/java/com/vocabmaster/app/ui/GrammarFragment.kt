package com.vocabmaster.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.vocabmaster.app.R
import com.vocabmaster.app.data.local.AppDatabase
import com.vocabmaster.app.data.repository.RealGrammarRepositoryImpl
import com.vocabmaster.app.ui.adapters.GrammarAdapter
import com.vocabmaster.app.ui.viewmodels.GrammarViewModel

class GrammarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_grammar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvGrammar = view.findViewById<RecyclerView>(R.id.rvGrammar)
        rvGrammar.layoutManager = LinearLayoutManager(requireContext())

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val emptyStateRoot = view.findViewById<View>(R.id.emptyStateRoot)
        val emptyStateIcon = view.findViewById<ImageView>(R.id.ivEmptyStateIcon)
        val emptyStateTitle = view.findViewById<TextView>(R.id.tvEmptyStateTitle)
        val emptyStateSubtitle = view.findViewById<TextView>(R.id.tvEmptyStateSubtitle)
        val emptyStateRetry = view.findViewById<MaterialButton>(R.id.btnEmptyStateRetry)

        val repo = RealGrammarRepositoryImpl(AppDatabase.getDatabase(requireContext()).grammarDao())
        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return GrammarViewModel(repo) as T
            }
        })[GrammarViewModel::class.java]

        emptyStateRetry.setOnClickListener { viewModel.loadTopics() }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            when {
                state.isLoading -> {
                    rvGrammar.visibility = View.GONE
                    emptyStateRoot.visibility = View.GONE
                }
                state.errorMessage != null -> {
                    rvGrammar.visibility = View.GONE
                    emptyStateRoot.visibility = View.VISIBLE
                    emptyStateIcon.setImageResource(android.R.drawable.ic_dialog_alert)
                    emptyStateTitle.text = getString(R.string.error_generic_title)
                    emptyStateSubtitle.text = getString(R.string.error_generic_subtitle)
                    emptyStateRetry.visibility = View.VISIBLE
                    Snackbar.make(view, R.string.error_generic_title, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.action_retry) { viewModel.loadTopics() }
                        .show()
                }
                state.items.isEmpty() -> {
                    rvGrammar.visibility = View.GONE
                    emptyStateRoot.visibility = View.VISIBLE
                    emptyStateIcon.setImageResource(android.R.drawable.ic_dialog_info)
                    emptyStateTitle.text = getString(R.string.grammar_empty_title)
                    emptyStateSubtitle.text = getString(R.string.grammar_empty_subtitle)
                    emptyStateRetry.visibility = View.GONE
                }
                else -> {
                    rvGrammar.visibility = View.VISIBLE
                    emptyStateRoot.visibility = View.GONE
                    rvGrammar.adapter = GrammarAdapter(state.items)
                }
            }
        }

        viewModel.loadTopics()
    }
}
