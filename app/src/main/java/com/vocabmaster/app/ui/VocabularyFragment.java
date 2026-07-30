package com.vocabmaster.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
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

        ShimmerLayout shimmerLayout = view.findViewById(R.id.shimmer_view_container);
        View emptyStateRoot = view.findViewById(R.id.emptyStateRoot);
        ImageView emptyStateIcon = view.findViewById(R.id.ivEmptyStateIcon);
        TextView emptyStateTitle = view.findViewById(R.id.tvEmptyStateTitle);
        TextView emptyStateSubtitle = view.findViewById(R.id.tvEmptyStateSubtitle);
        MaterialButton emptyStateRetry = view.findViewById(R.id.btnEmptyStateRetry);

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

        shimmerLayout.startShimmerAnimation();
        emptyStateRetry.setOnClickListener(v -> viewModel.loadWords());

        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                shimmerLayout.setVisibility(View.VISIBLE);
                shimmerLayout.startShimmerAnimation();
                rvVocabulary.setVisibility(View.GONE);
                emptyStateRoot.setVisibility(View.GONE);
                return;
            }

            shimmerLayout.stopShimmerAnimation();
            shimmerLayout.setVisibility(View.GONE);

            if (state.getErrorMessage() != null) {
                rvVocabulary.setVisibility(View.GONE);
                emptyStateRoot.setVisibility(View.VISIBLE);
                emptyStateIcon.setImageResource(android.R.drawable.ic_dialog_alert);
                emptyStateTitle.setText(R.string.error_generic_title);
                emptyStateSubtitle.setText(R.string.error_generic_subtitle);
                emptyStateRetry.setVisibility(View.VISIBLE);
                Snackbar.make(view, R.string.error_generic_title, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.action_retry, v -> viewModel.loadWords())
                        .show();
                return;
            }

            if (state.getItems().isEmpty()) {
                rvVocabulary.setVisibility(View.GONE);
                emptyStateRoot.setVisibility(View.VISIBLE);
                emptyStateIcon.setImageResource(android.R.drawable.ic_dialog_info);
                emptyStateTitle.setText(R.string.vocabulary_empty_title);
                emptyStateSubtitle.setText(R.string.vocabulary_empty_subtitle);
                emptyStateRetry.setVisibility(View.GONE);
                return;
            }

            emptyStateRoot.setVisibility(View.GONE);
            rvVocabulary.setVisibility(View.VISIBLE);

            VocabularyAdapter adapter = new VocabularyAdapter(
                    state.getItems(),
                    word -> {
                        viewModel.markWordAsLearned(word.getId());
                        Snackbar.make(view, getString(R.string.vocabulary_word_learned), Snackbar.LENGTH_SHORT).show();
                    },
                    word -> {
                        viewModel.deleteWord(word.getId());
                        Snackbar.make(view, getString(R.string.vocabulary_word_deleted), Snackbar.LENGTH_SHORT).show();
                    }
            );

            // Animasyon paket çakışmasını önlemek için doğrudan adaptörü set ediyoruz
            // Eğer AlphaInAnimationAdapter kullanılacaksa, kütüphane versiyonuyla uyumlu paket adı kullanılmalı
            rvVocabulary.setAdapter(adapter);

            // Custom item animator
            rvVocabulary.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        });

        viewModel.loadWords();
    }
}
