package com.vocabmaster.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.vocabmaster.app.ExamTakingActivity;
import com.vocabmaster.app.R;
import com.vocabmaster.app.data.repository.FirebaseExamRepositoryImpl;
import com.vocabmaster.app.domain.repository.ExamRepository;
import com.vocabmaster.app.ui.adapters.ExamAdapter;
import com.vocabmaster.app.ui.viewmodels.ExamViewModel;

public class ExamFragment extends Fragment {

    private ExamViewModel examViewModel;
    private RecyclerView rvExams;
    private View tvEmptyState;

    private final ActivityResultLauncher<Intent> examTakingLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> examViewModel.loadExams());

    public ExamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exam, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvExams = view.findViewById(R.id.rvExams);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        rvExams.setLayoutManager(new LinearLayoutManager(getContext()));

        ExamRepository repo = new FirebaseExamRepositoryImpl(FirebaseFirestore.getInstance());

        examViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ExamViewModel(repo);
            }
        }).get(ExamViewModel.class);

        examViewModel.getExams().observe(getViewLifecycleOwner(), exams -> {
            tvEmptyState.setVisibility(exams.isEmpty() ? View.VISIBLE : View.GONE);
            rvExams.setVisibility(exams.isEmpty() ? View.GONE : View.VISIBLE);

            ExamAdapter adapter = new ExamAdapter(exams, exam ->
                    examTakingLauncher.launch(ExamTakingActivity.createIntent(requireContext(), exam.getId(), exam.getTitle())));
            rvExams.setAdapter(adapter);
        });

        examViewModel.loadExams();
    }
}
