package com.vocabmaster.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.vocabmaster.app.R;

public class ExamFragment extends Fragment {

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
        
        androidx.recyclerview.widget.RecyclerView rvExams = view.findViewById(R.id.rvExams);
        rvExams.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        
        // Placeholder for ViewModel connection
        // examViewModel.getExams().observe(getViewLifecycleOwner(), exams -> {
        //     adapter.setExams(exams);
        //     rvExams.setAdapter(adapter);
        // });
        
        java.util.List<com.vocabmaster.app.domain.models.Exam> emptyExams = new java.util.ArrayList<>();
        com.vocabmaster.app.ui.adapters.ExamAdapter adapter = new com.vocabmaster.app.ui.adapters.ExamAdapter(emptyExams);
        rvExams.setAdapter(adapter);

        view.findViewById(R.id.btnSimulateSuccess).setOnClickListener(v -> {
            com.github.jinatonic.confetti.CommonConfetti.rainingConfetti(
                    (ViewGroup) view.getRootView(),
                    new int[] {
                        android.graphics.Color.parseColor("#4F46E5"), // Primary
                        android.graphics.Color.parseColor("#10B981"), // Accent
                        android.graphics.Color.parseColor("#F59E0B"), // Yellow
                        android.graphics.Color.parseColor("#EC4899")  // Pink
                    }
            ).oneShot();
        });
    }
}
