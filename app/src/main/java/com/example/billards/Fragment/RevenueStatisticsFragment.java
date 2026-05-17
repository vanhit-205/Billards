package com.example.billards.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.billards.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RevenueStatisticsFragment extends Fragment {

    private RadioGroup rgFilter;
    private TextView tvTotalRevenue;
    private BarChart barChart;
    private FirebaseFirestore db;
    private double totalRevenue = 0;
    private Map<Integer, Double> chartData = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_revenue_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        rgFilter = view.findViewById(R.id.rgFilter);
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        barChart = view.findViewById(R.id.barChart);

        setupChart();
        loadRevenueData("day");

        rgFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDay) {
                loadRevenueData("day");
            } else if (checkedId == R.id.rbMonth) {
                loadRevenueData("month");
            } else if (checkedId == R.id.rbYear) {
                loadRevenueData("year");
            }
        });
    }

    private void setupChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.getLegend().setEnabled(false);
        
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
    }

    private void loadRevenueData(String type) {
        Calendar calendar = Calendar.getInstance();
        long startTime;

        if ("day".equals(type)) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            startTime = calendar.getTimeInMillis();
        } else if ("month".equals(type)) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            startTime = calendar.getTimeInMillis();
        } else {
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            startTime = calendar.getTimeInMillis();
        }

        db.collection("payments")
                .whereGreaterThanOrEqualTo("time", startTime)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    totalRevenue = 0;
                    chartData.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Double price = doc.getDouble("price");
                            Long time = doc.getLong("time");
                            if (price != null && time != null) {
                                totalRevenue += price;
                                
                                Calendar itemCal = Calendar.getInstance();
                                itemCal.setTimeInMillis(time);
                                int key;
                                if ("day".equals(type)) {
                                    key = itemCal.get(Calendar.HOUR_OF_DAY);
                                } else if ("month".equals(type)) {
                                    key = itemCal.get(Calendar.DAY_OF_MONTH);
                                } else {
                                    key = itemCal.get(Calendar.MONTH) + 1; // Month in Java Calendar is 0-11
                                }
                                chartData.put(key, chartData.getOrDefault(key, 0.0) + price);
                            }
                        }
                        updateUI(type);
                    }
                });
    }

    private void updateUI(String type) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalRevenue.setText(formatter.format(totalRevenue));

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        if ("day".equals(type)) {
            for (int i = 0; i < 24; i++) {
                float val = chartData.getOrDefault(i, 0.0).floatValue();
                entries.add(new BarEntry(i, val));
                labels.add(i + "h");
            }
        } else if ("month".equals(type)) {
            Calendar cal = Calendar.getInstance();
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i <= daysInMonth; i++) {
                float val = chartData.getOrDefault(i, 0.0).floatValue();
                entries.add(new BarEntry(i - 1, val)); // entries use index 0-based for x
                labels.add(String.valueOf(i));
            }
        } else {
            for (int i = 1; i <= 12; i++) {
                float val = chartData.getOrDefault(i, 0.0).floatValue();
                entries.add(new BarEntry(i - 1, val));
                labels.add("T" + i);
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setLabelCount(labels.size() > 15 ? 10 : labels.size());
        barChart.invalidate(); // refresh
        barChart.animateY(1000);
    }
}
