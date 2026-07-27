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
        new Handler().postDelayed(() -> {
            if (isAdded() && shimmerLayout != null) {
                shimmerLayout.stopShimmerAnimation();
                shimmerLayout.setVisibility(View.GONE);
                
                // Show actual RecyclerView
                rvModules.setVisibility(View.VISIBLE);
                
                rvModules.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
                java.util.List<com.vocabmaster.app.domain.models.Module> modules = new java.util.ArrayList<>();
                modules.add(new com.vocabmaster.app.domain.models.Module("1", "Vocabulary", "Learn new words", R.mipmap.ic_launcher));
                modules.add(new com.vocabmaster.app.domain.models.Module("2", "Grammar", "Master grammar rules", R.mipmap.ic_launcher));
                modules.add(new com.vocabmaster.app.domain.models.Module("3", "Reading", "Read passages", R.mipmap.ic_launcher));
                
                com.vocabmaster.app.ui.adapters.ModuleAdapter adapter = new com.vocabmaster.app.ui.adapters.ModuleAdapter(modules);
                rvModules.setAdapter(adapter);
                
                // Show TapTargetView tutorial after loading
                showTutorial();
            }
        }, 1500); // reduced delay
    }
    
    private void showTutorial() {
        if (getActivity() == null) return;
        TapTargetView.showFor(getActivity(),
            TapTarget.forView(kbvHeader, "Hoş Geldiniz!", "Kategorileri keşfederek öğrenmeye başlayın.")
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
}
