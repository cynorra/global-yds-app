package com.vocabmaster.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.vocabmaster.app.LoginActivity;
import com.vocabmaster.app.R;
import com.vocabmaster.app.data.local.AppDatabase;
import com.vocabmaster.app.data.remote.RetrofitClient;
import com.vocabmaster.app.data.repository.FirebaseExamRepositoryImpl;
import com.vocabmaster.app.data.repository.RealVocabularyRepositoryImpl;
import com.vocabmaster.app.domain.repository.ExamRepository;
import com.vocabmaster.app.domain.repository.VocabularyRepository;
import com.vocabmaster.app.ui.adapters.AchievementAdapter;
import com.vocabmaster.app.ui.viewmodels.ProfileStats;
import com.vocabmaster.app.ui.viewmodels.ProfileViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private LineChart chart;
    private TextView tvNoExamsYet;
    private RecyclerView rvAchievements;

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
        rvAchievements = view.findViewById(R.id.rvAchievements);
        rvAchievements.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        bindUserInfo(view);

        VocabularyRepository vocabularyRepository = new RealVocabularyRepositoryImpl(
                RetrofitClient.INSTANCE.getApi(),
                AppDatabase.getDatabase(requireContext()).wordDao(),
                FirebaseFirestore.getInstance(),
                requireContext()
        );
        ExamRepository examRepository = new FirebaseExamRepositoryImpl(FirebaseFirestore.getInstance(), RetrofitClient.INSTANCE.getApi());

        ProfileViewModel viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ProfileViewModel(vocabularyRepository, examRepository, FirebaseFirestore.getInstance());
            }
        }).get(ProfileViewModel.class);

        viewModel.getStats().observe(getViewLifecycleOwner(), this::bindStats);
        viewModel.loadStats();

        view.findViewById(R.id.tvManagePremium).setOnClickListener(v ->
                startActivity(com.vocabmaster.app.PremiumActivity.createIntent(requireContext())));

        com.vocabmaster.app.data.billing.BillingManager.isPremium().observe(getViewLifecycleOwner(), isPremium -> {
            view.findViewById(R.id.tvPremiumBadge).setVisibility(isPremium ? View.VISIBLE : View.GONE);
        });

        view.findViewById(R.id.tvAccountSettings).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), com.vocabmaster.app.AccountSettingsActivity.class)));

        view.findViewById(R.id.tvNotifications).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), com.vocabmaster.app.NotificationSettingsActivity.class)));

        view.findViewById(R.id.tvAbout).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), com.vocabmaster.app.AboutActivity.class)));

        view.findViewById(R.id.tvLanguage).setOnClickListener(v -> showLanguageDialog());

        view.findViewById(R.id.tvLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void showLanguageDialog() {
        List<String> languages = new ArrayList<>();
        languages.add(getString(R.string.lang_english));
        languages.add(getString(R.string.lang_turkish));

        new MaterialDialog(requireContext(), MaterialDialog.getDEFAULT_BEHAVIOR())
                .title(null, getString(R.string.dialog_language_title))
                .show(dialog -> {
                    DialogListExtKt.listItems(dialog, null, languages, null, true, (dlg, index, text) -> {
                        String languageCode = index == 0 ? "en" : "tr";
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode));
                        return null;
                    });
                    return null;
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
        TextView tvLevel = requireView().findViewById(R.id.tvLevel);
        TextView tvStreak = requireView().findViewById(R.id.tvStreak);

        tvWordsCount.setText(String.valueOf(stats.getLearnedWordsCount()));
        tvExamsCount.setText(String.valueOf(stats.getCompletedExamScores().size()));
        tvLevel.setText(String.valueOf(stats.getLevel()));
        tvStreak.setText(String.valueOf(stats.getCurrentStreak()));

        rvAchievements.setAdapter(new AchievementAdapter(stats.getAchievements()));

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

        int brandColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.colorPrimary);
        int accentColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.colorAccent);

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.exam_result_label));
        dataSet.setColor(brandColor);
        dataSet.setCircleColor(brandColor);
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Gradient fill using the app's own brand colors, not the generic Material purple this
        // chart-library boilerplate shipped with.
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(accentColor);
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
