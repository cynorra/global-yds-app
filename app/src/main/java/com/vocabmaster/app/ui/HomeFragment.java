package com.vocabmaster.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.vocabmaster.app.MainActivity;
import com.vocabmaster.app.R;
import com.vocabmaster.app.data.local.AppPreferences;
import com.vocabmaster.app.domain.models.ExamType;
import com.vocabmaster.app.ui.adapters.HomeExamCardAdapter;
import com.vocabmaster.app.ui.adapters.HomeExamItem;
import com.vocabmaster.app.ui.adapters.ModuleAdapter;
import io.supercharge.shimmerlayout.ShimmerLayout;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ShimmerLayout shimmerLayout;
    private RecyclerView rvModules;
    private RecyclerView rvExamHub;
    private KenBurnsView kbvHeader;
    private final Handler loadHandler = new Handler(android.os.Looper.getMainLooper());

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        rvModules = view.findViewById(R.id.rvModules);
        rvExamHub = view.findViewById(R.id.rvExamHub);
        kbvHeader = view.findViewById(R.id.kbvHeader);

        shimmerLayout.startShimmerAnimation();

        // Setup Global Exam Hub
        setupExamHub();

        // Setup bottom chip group
        setupTopicChips(view);

        // Simulate network request loading time
        loadHandler.postDelayed(() -> {
            if (isAdded() && shimmerLayout != null) {
                shimmerLayout.stopShimmerAnimation();
                shimmerLayout.setVisibility(View.GONE);

                // Show actual RecyclerView
                rvModules.setVisibility(View.VISIBLE);

                rvModules.setLayoutManager(new LinearLayoutManager(getContext()));
                List<com.vocabmaster.app.domain.models.Module> modules = new ArrayList<>();
                modules.add(new com.vocabmaster.app.domain.models.Module("1", getString(R.string.topic_vocabulary), getString(R.string.module_vocabulary_desc), R.drawable.ic_module_vocabulary));
                modules.add(new com.vocabmaster.app.domain.models.Module("2", getString(R.string.topic_grammar), getString(R.string.module_grammar_desc), R.drawable.ic_module_grammar));
                modules.add(new com.vocabmaster.app.domain.models.Module("3", getString(R.string.topic_reading), getString(R.string.module_reading_desc), R.drawable.ic_module_reading));

                ModuleAdapter adapter = new ModuleAdapter(modules, module -> {
                    if (!(getActivity() instanceof MainActivity)) return;
                    MainActivity activity = (MainActivity) getActivity();
                    switch (module.getId()) {
                        case "1":
                            activity.selectBottomNavTab(R.id.nav_vocabulary);
                            break;
                        case "2":
                            activity.openFragment(new GrammarFragment(), true);
                            break;
                        case "3":
                            activity.openFragment(new ReadingFragment(), true);
                            break;
                    }
                });
                rvModules.setAdapter(adapter);

                showTutorial();
            }
        }, 1200);
    }

    private void setupExamHub() {
        if (rvExamHub == null) return;
        rvExamHub.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<HomeExamItem> examItems = new ArrayList<>();
        examItems.add(new HomeExamItem(ExamType.TOEIC, getString(R.string.exam_candidates_format, "~7M")));
        examItems.add(new HomeExamItem(ExamType.IELTS, getString(R.string.exam_candidates_format, "~4M")));
        examItems.add(new HomeExamItem(ExamType.TOEFL, getString(R.string.exam_candidates_format, "~3M")));
        examItems.add(new HomeExamItem(ExamType.CAMBRIDGE, getString(R.string.exam_candidates_format, "~6M")));
        examItems.add(new HomeExamItem(ExamType.SAT, getString(R.string.exam_candidates_format, "~2M")));
        examItems.add(new HomeExamItem(ExamType.PTE, getString(R.string.exam_candidates_format, "~2M")));
        examItems.add(new HomeExamItem(ExamType.DET, getString(R.string.exam_candidates_format, "~2M")));
        examItems.add(new HomeExamItem(ExamType.EIKEN, getString(R.string.exam_candidates_format, "~3.5M")));
        examItems.add(new HomeExamItem(ExamType.GRE, getString(R.string.exam_candidates_format, "~500K")));
        examItems.add(new HomeExamItem(ExamType.YDS, getString(R.string.exam_candidates_format, "~400K")));

        HomeExamCardAdapter examAdapter = new HomeExamCardAdapter(examItems, examType -> {
            AppPreferences.setTargetExam(requireContext(), examType.getCode());
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.selectBottomNavTab(R.id.nav_exam);
            }
        });
        rvExamHub.setAdapter(examAdapter);
    }

    private void setupTopicChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.chipGroupTopics);
        if (chipGroup == null) return;

        view.findViewById(R.id.chipTopicGrammar).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openFragment(new GrammarFragment(), true);
            }
        });
        view.findViewById(R.id.chipTopicVocabulary).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectBottomNavTab(R.id.nav_vocabulary);
            }
        });
        view.findViewById(R.id.chipTopicMockExam).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectBottomNavTab(R.id.nav_exam);
            }
        });
    }

    private void showTutorial() {
        if (getActivity() == null) return;
        TapTargetView.showFor(getActivity(),
            TapTarget.forView(kbvHeader, getString(R.string.home_tutorial_title), getString(R.string.home_tutorial_desc))
                .outerCircleColor(R.color.colorPrimary)
                .targetCircleColor(android.R.color.white)
                .titleTextSize(20)
                .titleTextColor(android.R.color.white)
                .descriptionTextSize(14)
                .descriptionTextColor(android.R.color.white)
                .cancelable(true)
                .transparentTarget(true)
                .targetRadius(100),
            new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                }
            });
    }

    @Override
    public void onDestroyView() {
        loadHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}
