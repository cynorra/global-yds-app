package com.vocabmaster.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.vocabmaster.app.R;

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
        
        com.vocabmaster.app.data.repository.RealVocabularyRepositoryImpl repo =
            new com.vocabmaster.app.data.repository.RealVocabularyRepositoryImpl(
                com.vocabmaster.app.data.remote.RetrofitClient.INSTANCE.getApi(),
                com.vocabmaster.app.data.local.AppDatabase.getDatabase(requireContext()).wordDao()
            );

        com.vocabmaster.app.ui.viewmodels.VocabularyViewModel viewModel = 
            new androidx.lifecycle.ViewModelProvider(this, new androidx.lifecycle.ViewModelProvider.Factory() {
                @NonNull
                @Override
                public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                    return (T) new com.vocabmaster.app.ui.viewmodels.VocabularyViewModel(repo);
                }
            }).get(com.vocabmaster.app.ui.viewmodels.VocabularyViewModel.class);
        
        io.supercharge.shimmerlayout.ShimmerLayout shimmerLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerLayout.startShimmerAnimation();
        
        viewModel.getWords().observe(getViewLifecycleOwner(), words -> {
            shimmerLayout.stopShimmerAnimation();
            shimmerLayout.setVisibility(View.GONE);
            rvVocabulary.setVisibility(View.VISIBLE);

            com.vocabmaster.app.ui.adapters.VocabularyAdapter adapter = new com.vocabmaster.app.ui.adapters.VocabularyAdapter(words);
            
            // Add Premium Animations to the cards
            jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter alphaAdapter = 
                new jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter(adapter);
            jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter scaleAdapter = 
                new jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter(alphaAdapter);
            
            scaleAdapter.setFirstOnly(false);
            scaleAdapter.setDuration(500);
            scaleAdapter.setInterpolator(new android.view.animation.OvershootInterpolator());
            
            rvVocabulary.setAdapter(scaleAdapter);
            
            // Add custom item animator
            rvVocabulary.setItemAnimator(new jp.wasabeef.recyclerview.animators.SlideInUpAnimator(new android.view.animation.OvershootInterpolator(1f)));
        });

        // Trigger the API Call
        viewModel.loadWords();
    }
}
