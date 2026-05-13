package com.example.billards.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.billards.Models.Payment;
import com.example.billards.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class RevenueStatisticsFragment extends Fragment {

    private RadioGroup rgFilter;
    private TextView tvTotalRevenue, tvChartPlaceholder;
    private FirebaseFirestore db;
    private double totalRevenue = 0;

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
        tvChartPlaceholder = view.findViewById(R.id.tvChartPlaceholder);

        loadRevenueData("day");

        rgFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDay) {
                loadRevenueData("day");
            } else if (checkedId == R.id.rbMonth) {
                loadRevenueData("month");
            }
        });
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
            tvChartPlaceholder.setText("Thống kê doanh thu hôm nay");
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            startTime = calendar.getTimeInMillis();
            tvChartPlaceholder.setText("Thống kê doanh thu tháng này");
        }

        db.collection("payments")
                .whereGreaterThanOrEqualTo("time", startTime)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    totalRevenue = 0;
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            // Cần ánh xạ đúng với model Payment
                            Double price = doc.getDouble("price");
                            if (price != null) {
                                totalRevenue += price;
                            }
                        }
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalRevenue.setText(formatter.format(totalRevenue));
    }
}
