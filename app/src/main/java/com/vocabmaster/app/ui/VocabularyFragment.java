package com.vocabmaster.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.vocabmaster.app.R;
import com.vocabmaster.app.data.local.AppDatabase;
import com.vocabmaster.app.data.remote.RetrofitClient;
import com.vocabmaster.app.data.repository.RealVocabularyRepositoryImpl;
import com.vocabmaster.app.ui.adapters.VocabularyAdapter;
import com.vocabmaster.app.ui.viewmodels.VocabularyViewModel;

import io.supercharge.shimmerlayout.ShimmerLayout;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

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
        
        RecyclerView rvVocabulary = view.findViewById(R.id.rvVocabulary);
        rvVocabulary.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        
        new PagerSnapHelper().attachToRecyclerView(rvVocabulary);
        
        RealVocabularyRepositoryImpl repo = new RealVocabularyRepositoryImpl(
                RetrofitClient.INSTANCE.getApi(),
                AppDatabase.getDatabase(requireContext()).wordDao()
        );

        VocabularyViewModel viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new VocabularyViewModel(repo);
            }
        }).get(VocabularyViewModel.class);
        
        ShimmerLayout shimmerLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerLayout.startShimmerAnimation();
        
        viewModel.getWords().observe(getViewLifecycleOwner(), words -> {
            shimmerLayout.stopShimmerAnimation();
            shimmerLayout.setVisibility(View.GONE);
            rvVocabulary.setVisibility(View.VISIBLE);

            VocabularyAdapter adapter = new VocabularyAdapter(words);
            
            // Animasyon paket çakışmasını önlemek için doğrudan adaptörü set ediyoruz
            // Eğer AlphaInAnimationAdapter kullanılacaksa, kütüphane versiyonuyla uyumlu paket adı kullanılmalı
            rvVocabulary.setAdapter(adapter);
            
            // Custom item animator
            rvVocabulary.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        });

        viewModel.loadWords();
    }
}
