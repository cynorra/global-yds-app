package com.vocabmaster.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.vocabmaster.app.LoginActivity;
import com.vocabmaster.app.R;
import com.vocabmaster.app.data.local.AppPreferences;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.List;
public class ProfileFragment extends Fragment {

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
        
        LineChart chart = view.findViewById(R.id.progressChart);
        setupChart(chart);

        view.findViewById(R.id.tvLogout).setOnClickListener(v -> {
            AppPreferences.setLoggedIn(requireContext(), false);
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }
    
    private void setupChart(LineChart chart) {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1f, 50f));
        entries.add(new Entry(2f, 65f));
        entries.add(new Entry(3f, 60f));
        entries.add(new Entry(4f, 80f));
        entries.add(new Entry(5f, 95f));
        
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
