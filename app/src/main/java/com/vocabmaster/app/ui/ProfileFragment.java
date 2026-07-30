package com.vocabmaster.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vocabmaster.app.LoginActivity;
import com.vocabmaster.app.R;
import com.vocabmaster.app.data.local.AppDatabase;
import com.vocabmaster.app.data.remote.RetrofitClient;
import com.vocabmaster.app.data.repository.FirebaseExamRepositoryImpl;
import com.vocabmaster.app.data.repository.RealVocabularyRepositoryImpl;
import com.vocabmaster.app.domain.repository.ExamRepository;
import com.vocabmaster.app.domain.repository.VocabularyRepository;
import com.vocabmaster.app.ui.viewmodels.ProfileStats;
import com.vocabmaster.app.ui.viewmodels.ProfileViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import android.graphics.Color;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private LineChart chart;
    private TextView tvNoExamsYet;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chart = view.findViewById(R.id.progressChart);
        tvNoExamsYet = view.findViewById(R.id.tvNoExamsYet);

        bindUserInfo(view);

        VocabularyRepository vocabularyRepository = new RealVocabularyRepositoryImpl(
                RetrofitClient.INSTANCE.getApi(),
                AppDatabase.getDatabase(requireContext()).wordDao(),
                FirebaseFirestore.getInstance()
        );
        ExamRepository examRepository = new FirebaseExamRepositoryImpl(FirebaseFirestore.getInstance());

        ProfileViewModel viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ProfileViewModel(vocabularyRepository, examRepository);
            }
        }).get(ProfileViewModel.class);

        viewModel.getStats().observe(getViewLifecycleOwner(), this::bindStats);
        viewModel.loadStats();

        view.findViewById(R.id.tvLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void bindUserInfo(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        if (user == null) return;

        String displayName = user.getDisplayName();
        if (displayName != null && !displayName.isEmpty()) {
            tvUsername.setText(displayName);
        } else if (user.getEmail() != null) {
            tvUsername.setText(user.getEmail().split("@")[0]);
        }
    }

    private void bindStats(ProfileStats stats) {
        TextView tvWordsCount = requireView().findViewById(R.id.tvWordsCount);
        TextView tvExamsCount = requireView().findViewById(R.id.tvExamsCount);

        tvWordsCount.setText(String.valueOf(stats.getLearnedWordsCount()));
        tvExamsCount.setText(String.valueOf(stats.getCompletedExamScores().size()));

        setupChart(chart, stats.getCompletedExamScores());
    }

    private void setupChart(LineChart chart, List<Integer> scores) {
        if (scores.isEmpty()) {
            chart.setVisibility(View.GONE);
            tvNoExamsYet.setVisibility(View.VISIBLE);
            return;
        }
        chart.setVisibility(View.VISIBLE);
        tvNoExamsYet.setVisibility(View.GONE);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            entries.add(new Entry(i + 1f, scores.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Sınav Puanı");
        dataSet.setColor(Color.parseColor("#6200EE"));
        dataSet.setCircleColor(Color.parseColor("#6200EE"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Premium gradient fill
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#BB86FC"));
        dataSet.setFillAlpha(100);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Styling
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.animateX(1000);
        chart.invalidate();
    }
}
