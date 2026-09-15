package com.vocabmaster.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.vocabmaster.app.R;
import io.supercharge.shimmerlayout.ShimmerLayout;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

public class HomeFragment extends Fragment {

    private ShimmerLayout shimmerLayout;
    private RecyclerView rvModules;
    private KenBurnsView kbvHeader;
    private final Handler loadHandler = new Handler(android.os.Looper.getMainLooper());

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        rvModules = view.findViewById(R.id.rvModules);
        kbvHeader = view.findViewById(R.id.kbvHeader);

        shimmerLayout.startShimmerAnimation();

        // Optional: you can pause/resume kbvHeader.pause() / resume() based on lifecycle if needed


        // Simulate network request loading time
        loadHandler.postDelayed(() -> {
            if (isAdded() && shimmerLayout != null) {
                shimmerLayout.stopShimmerAnimation();
                shimmerLayout.setVisibility(View.GONE);
                
                // Show actual RecyclerView
                rvModules.setVisibility(View.VISIBLE);
                
                rvModules.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
                java.util.List<com.vocabmaster.app.domain.models.Module> modules = new java.util.ArrayList<>();
                modules.add(new com.vocabmaster.app.domain.models.Module("1", getString(R.string.topic_vocabulary), getString(R.string.module_vocabulary_desc), R.drawable.ic_module_vocabulary));
                modules.add(new com.vocabmaster.app.domain.models.Module("2", getString(R.string.topic_grammar), getString(R.string.module_grammar_desc), R.drawable.ic_module_grammar));
                modules.add(new com.vocabmaster.app.domain.models.Module("3", getString(R.string.topic_reading), getString(R.string.module_reading_desc), R.drawable.ic_module_reading));
                
                com.vocabmaster.app.ui.adapters.ModuleAdapter adapter = new com.vocabmaster.app.ui.adapters.ModuleAdapter(modules, module -> {
                    if (!(getActivity() instanceof com.vocabmaster.app.MainActivity)) return;
                    com.vocabmaster.app.MainActivity activity = (com.vocabmaster.app.MainActivity) getActivity();
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
                
                // Show TapTargetView tutorial after loading
                showTutorial();
            }
        }, 1500); // reduced delay
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
