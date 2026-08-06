package com.vocabmaster.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vocabmaster.app.ExamTakingActivity;
import com.vocabmaster.app.R;
import com.vocabmaster.app.data.remote.RetrofitClient;
import com.vocabmaster.app.data.repository.FirebaseExamRepositoryImpl;
import com.vocabmaster.app.domain.repository.ExamRepository;
import com.vocabmaster.app.ui.adapters.ExamAdapter;
import com.vocabmaster.app.ui.viewmodels.ExamViewModel;

public class ExamFragment extends Fragment {

    private ExamViewModel examViewModel;
    private RecyclerView rvExams;
    private ProgressBar progressBar;
    private View emptyStateRoot;
    private ImageView emptyStateIcon;
    private TextView emptyStateTitle;
    private TextView emptyStateSubtitle;
    private MaterialButton emptyStateRetry;

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
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateRoot = view.findViewById(R.id.emptyStateRoot);
        emptyStateIcon = view.findViewById(R.id.ivEmptyStateIcon);
        emptyStateTitle = view.findViewById(R.id.tvEmptyStateTitle);
        emptyStateSubtitle = view.findViewById(R.id.tvEmptyStateSubtitle);
        emptyStateRetry = view.findViewById(R.id.btnEmptyStateRetry);
        rvExams.setLayoutManager(new LinearLayoutManager(getContext()));

        ExamRepository repo = new FirebaseExamRepositoryImpl(FirebaseFirestore.getInstance(), RetrofitClient.INSTANCE.getApi());

        examViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ExamViewModel(repo);
            }
        }).get(ExamViewModel.class);

        emptyStateRetry.setOnClickListener(v -> examViewModel.loadExams());

        examViewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);

            if (state.isLoading()) {
                rvExams.setVisibility(View.GONE);
                emptyStateRoot.setVisibility(View.GONE);
                return;
            }

            if (state.getErrorMessage() != null) {
                rvExams.setVisibility(View.GONE);
                emptyStateRoot.setVisibility(View.VISIBLE);
                emptyStateIcon.setImageResource(R.drawable.ic_state_error);
                emptyStateTitle.setText(R.string.exam_error_title);
                emptyStateSubtitle.setText(R.string.error_generic_subtitle);
                emptyStateRetry.setVisibility(View.VISIBLE);
                Snackbar.make(view, R.string.exam_error_title, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.action_retry, v -> examViewModel.loadExams())
                        .show();
                return;
            }

            if (state.getItems().isEmpty()) {
                rvExams.setVisibility(View.GONE);
                emptyStateRoot.setVisibility(View.VISIBLE);
                emptyStateIcon.setImageResource(R.drawable.ic_state_empty);
                emptyStateTitle.setText(R.string.exam_no_exams);
                emptyStateSubtitle.setText("");
                emptyStateRetry.setVisibility(View.GONE);
                return;
            }

            emptyStateRoot.setVisibility(View.GONE);
            rvExams.setVisibility(View.VISIBLE);

            ExamAdapter adapter = new ExamAdapter(state.getItems(), exam ->
                    examTakingLauncher.launch(ExamTakingActivity.createIntent(requireContext(), exam.getId(), exam.getTitle())));
            rvExams.setAdapter(adapter);
        });

        examViewModel.loadExams();
    }
}
