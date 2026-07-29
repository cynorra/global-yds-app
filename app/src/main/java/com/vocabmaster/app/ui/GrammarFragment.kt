package com.vocabmaster.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        val repo = RealGrammarRepositoryImpl(AppDatabase.getDatabase(requireContext()).grammarDao())
        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return GrammarViewModel(repo) as T
            }
        })[GrammarViewModel::class.java]

        viewModel.topics.observe(viewLifecycleOwner) { topics ->
            rvGrammar.adapter = GrammarAdapter(topics)
        }

        viewModel.loadTopics()
    }
}
