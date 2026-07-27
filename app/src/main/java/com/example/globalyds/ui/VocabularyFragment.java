package com.example.globalyds.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.globalyds.R;

public class VocabularyFragment extends Fragment {

    public VocabularyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vocabulary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        androidx.recyclerview.widget.RecyclerView rvVocabulary = view.findViewById(R.id.rvVocabulary);
        rvVocabulary.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        
        // Use PagerSnapHelper to snap to items like cards
        new androidx.recyclerview.widget.PagerSnapHelper().attachToRecyclerView(rvVocabulary);
        
        com.example.globalyds.data.repository.RealVocabularyRepositoryImpl repo = 
            new com.example.globalyds.data.repository.RealVocabularyRepositoryImpl(
                com.example.globalyds.data.remote.RetrofitClient.INSTANCE.getApi()
            );

        com.example.globalyds.ui.viewmodels.VocabularyViewModel viewModel = 
            new androidx.lifecycle.ViewModelProvider(this, new androidx.lifecycle.ViewModelProvider.Factory() {
                @NonNull
                @Override
                public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                    return (T) new com.example.globalyds.ui.viewmodels.VocabularyViewModel(repo);
                }
            }).get(com.example.globalyds.ui.viewmodels.VocabularyViewModel.class);
        
        io.supercharge.shimmerlayout.ShimmerLayout shimmerLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerLayout.startShimmerAnimation();
        
        viewModel.getWords().observe(getViewLifecycleOwner(), words -> {
            shimmerLayout.stopShimmerAnimation();
            shimmerLayout.setVisibility(View.GONE);
            rvVocabulary.setVisibility(View.VISIBLE);

            com.example.globalyds.ui.adapters.VocabularyAdapter adapter = new com.example.globalyds.ui.adapters.VocabularyAdapter(words);
            rvVocabulary.setAdapter(adapter);
        });

        // Trigger the API Call
        viewModel.loadWords();
    }
}
