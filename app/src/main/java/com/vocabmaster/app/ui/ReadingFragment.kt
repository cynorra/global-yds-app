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
import com.vocabmaster.app.data.repository.RealReadingRepositoryImpl
import com.vocabmaster.app.ui.adapters.ReadingAdapter
import com.vocabmaster.app.ui.viewmodels.ReadingViewModel

class ReadingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_reading, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvReading = view.findViewById<RecyclerView>(R.id.rvReading)
        rvReading.layoutManager = LinearLayoutManager(requireContext())

        val repo = RealReadingRepositoryImpl(AppDatabase.getDatabase(requireContext()).readingDao())
        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ReadingViewModel(repo) as T
            }
        })[ReadingViewModel::class.java]

        viewModel.passages.observe(viewLifecycleOwner) { passages ->
            rvReading.adapter = ReadingAdapter(passages)
        }

        viewModel.loadPassages()
    }
}
