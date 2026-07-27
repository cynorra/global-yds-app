package com.example.globalyds.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globalyds.R;
import io.supercharge.shimmerlayout.ShimmerLayout;

public class HomeFragment extends Fragment {

    private ShimmerLayout shimmerLayout;
    private RecyclerView rvModules;

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

        shimmerLayout.startShimmerAnimation();

        // Simulate network request loading time
        new Handler().postDelayed(() -> {
            if (isAdded() && shimmerLayout != null) {
                shimmerLayout.stopShimmerAnimation();
                shimmerLayout.setVisibility(View.GONE);
                
                // Show actual RecyclerView
                rvModules.setVisibility(View.VISIBLE);
                
                rvModules.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
                java.util.List<com.example.globalyds.domain.models.Module> modules = new java.util.ArrayList<>();
                modules.add(new com.example.globalyds.domain.models.Module("1", "Vocabulary", "Learn new words", R.mipmap.ic_launcher));
                modules.add(new com.example.globalyds.domain.models.Module("2", "Grammar", "Master grammar rules", R.mipmap.ic_launcher));
                modules.add(new com.example.globalyds.domain.models.Module("3", "Reading", "Read passages", R.mipmap.ic_launcher));
                
                com.example.globalyds.ui.adapters.ModuleAdapter adapter = new com.example.globalyds.ui.adapters.ModuleAdapter(modules);
                rvModules.setAdapter(adapter);
            }
        }, 1500); // reduced delay
    }
}
