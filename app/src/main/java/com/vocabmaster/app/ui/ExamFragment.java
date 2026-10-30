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
import com.google.android.material.card.MaterialCardView;
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
    
    private MaterialCardView cardMistakes;
    private TextView tvMistakesCount;
    private MaterialCardView cardMicroExam;
    private TextView tvWeakAreaInsight;

    private static final int MICRO_EXAM_DURATION_MINUTES = 6;

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
        cardMistakes = view.findViewById(R.id.cardMistakes);
        tvMistakesCount = view.findViewById(R.id.tvMistakesCount);
        cardMicroExam = view.findViewById(R.id.cardMicroExam);
        tvWeakAreaInsight = view.findViewById(R.id.tvWeakAreaInsight);
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

        com.google.android.material.chip.ChipGroup chipGroup = view.findViewById(R.id.chipGroupExamFilter);
        if (chipGroup != null) {
            String initialFilter = null;
            if (getArguments() != null && getArguments().getString("EXAM_FILTER") != null) {
                initialFilter = getArguments().getString("EXAM_FILTER");
            } else {
                initialFilter = com.vocabmaster.app.data.local.AppPreferences.getTargetExam(requireContext());
            }

            if ("TOEIC".equalsIgnoreCase(initialFilter)) {
                chipGroup.check(R.id.chipToeic);
                examViewModel.filterByExamType("TOEIC");
            } else if ("IELTS".equalsIgnoreCase(initialFilter)) {
                chipGroup.check(R.id.chipIelts);
                examViewModel.filterByExamType("IELTS");
            } else if ("TOEFL".equalsIgnoreCase(initialFilter)) {
                chipGroup.check(R.id.chipToefl);
                examViewModel.filterByExamType("TOEFL");
            } else if ("CAMBRIDGE".equalsIgnoreCase(initialFilter)) {
                chipGroup.check(R.id.chipCambridge);
                examViewModel.filterByExamType("CAMBRIDGE");
            } else if ("SAT".equalsIgnoreCase(initialFilter)) {
                chipGroup.check(R.id.chipSat);
                examViewModel.filterByExamType("SAT");
            } else if ("PTE".equalsIgnoreCase(initialFilter)) {
                chipGroup.check(R.id.chipPte);
                examViewModel.filterByExamType("PTE");
            } else if ("DET".equalsIgnoreCase(initialFilter)) {
                chipGroup.check(R.id.chipDet);
                examViewModel.filterByExamType("DET");
            } else if ("EIKEN".equalsIgnoreCase(initialFilter)) {
                chipGroup.check(R.id.chipEiken);
                examViewModel.filterByExamType("EIKEN");
            } else if ("GRE".equalsIgnoreCase(initialFilter)) {
                chipGroup.check(R.id.chipGre);
                examViewModel.filterByExamType("GRE");
            } else if ("YDS".equalsIgnoreCase(initialFilter)) {
                chipGroup.check(R.id.chipYds);
                examViewModel.filterByExamType("YDS");
            } else {
                chipGroup.check(R.id.chipAll);
            }

            chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) {
                    examViewModel.filterByExamType("ALL");
                    return;
                }
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipToeic) {
                    examViewModel.filterByExamType("TOEIC");
                } else if (checkedId == R.id.chipIelts) {
                    examViewModel.filterByExamType("IELTS");
                } else if (checkedId == R.id.chipToefl) {
                    examViewModel.filterByExamType("TOEFL");
                } else if (checkedId == R.id.chipCambridge) {
                    examViewModel.filterByExamType("CAMBRIDGE");
                } else if (checkedId == R.id.chipSat) {
                    examViewModel.filterByExamType("SAT");
                } else if (checkedId == R.id.chipPte) {
                    examViewModel.filterByExamType("PTE");
                } else if (checkedId == R.id.chipDet) {
                    examViewModel.filterByExamType("DET");
                } else if (checkedId == R.id.chipEiken) {
                    examViewModel.filterByExamType("EIKEN");
                } else if (checkedId == R.id.chipGre) {
                    examViewModel.filterByExamType("GRE");
                } else if (checkedId == R.id.chipYds) {
                    examViewModel.filterByExamType("YDS");
                } else {
                    examViewModel.filterByExamType("ALL");
                }
            });
        }

        cardMicroExam.setOnClickListener(v -> {
            Intent intent = ExamTakingActivity.createIntent(requireContext(), "MICRO", getString(R.string.micro_exam_title), MICRO_EXAM_DURATION_MINUTES);
            examTakingLauncher.launch(intent);
        });

        cardMistakes.setOnClickListener(v -> {
            Intent intent = ExamTakingActivity.createIntent(requireContext(), "MISTAKES", getString(R.string.mistakes_box_title));
            examTakingLauncher.launch(intent);
        });

        examViewModel.getWeakAreaInsight().observe(getViewLifecycleOwner(), insight -> {
            if (insight == null) {
                tvWeakAreaInsight.setVisibility(View.GONE);
                return;
            }
            tvWeakAreaInsight.setVisibility(View.VISIBLE);
            if (insight.getScorePercent() == null) {
                tvWeakAreaInsight.setText(getString(R.string.weak_area_not_attempted, insight.getTopicTitle()));
            } else {
                tvWeakAreaInsight.setText(getString(R.string.weak_area_low_score, insight.getTopicTitle(), insight.getScorePercent()));
            }
        });

        examViewModel.getMistakesCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null && count > 0) {
                cardMistakes.setVisibility(View.VISIBLE);
                tvMistakesCount.setText(getString(R.string.mistakes_box_desc, count));
            } else {
                cardMistakes.setVisibility(View.GONE);
            }
        });

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
                if (!com.vocabmaster.app.util.NetworkUtils.isOnline(requireContext())) {
                    emptyStateSubtitle.setText(R.string.network_offline_exam_hint);
                } else {
                    emptyStateSubtitle.setText(R.string.error_generic_subtitle);
                }
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
                    examTakingLauncher.launch(ExamTakingActivity.createIntent(requireContext(), exam.getId(), exam.getTitle(), exam.getDurationMinutes())));
            rvExams.setAdapter(adapter);
        });

        examViewModel.loadExams();
    }
}
