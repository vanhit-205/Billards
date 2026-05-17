package com.example.billards.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.Models.Payment;
import com.example.billards.Models.PaymentHistoryAdapter;
import com.example.billards.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PaymentHistoryFragment extends Fragment {

    private RecyclerView rvPaymentHistory;
    private TextView tvEmptyHistory;
    private PaymentHistoryAdapter adapter;
    private List<Payment> paymentList;
    private FirebaseFirestore db;

    public PaymentHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPaymentHistory = view.findViewById(R.id.rvPaymentHistory);
        tvEmptyHistory = view.findViewById(R.id.tvEmptyHistory);

        paymentList = new ArrayList<>();
        rvPaymentHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PaymentHistoryAdapter(paymentList, getContext());
        rvPaymentHistory.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadPaymentHistory();
    }

    private void loadPaymentHistory() {
        db.collection("payments")
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("PaymentHistory", "Error fetching payments", error);
                        return;
                    }

                    if (value != null) {
                        paymentList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Payment payment = parsePayment(doc);
                            paymentList.add(payment);
                        }

                        if (paymentList.isEmpty()) {
                            tvEmptyHistory.setVisibility(View.VISIBLE);
                            rvPaymentHistory.setVisibility(View.GONE);
                        } else {
                            tvEmptyHistory.setVisibility(View.GONE);
                            rvPaymentHistory.setVisibility(View.VISIBLE);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private Payment parsePayment(QueryDocumentSnapshot doc) {
        Payment p = new Payment();
        p.setId(doc.getId());

        Long tableVal = doc.getLong("table");
        p.setTable(tableVal != null ? tableVal.intValue() : 0);

        Long timeVal = doc.getLong("time");
        p.setTime(timeVal != null ? timeVal : 0L);

        Long timePlayVal = doc.getLong("timePlay");
        p.setTimePlay(timePlayVal != null ? timePlayVal : 0L);

        Double priceVal = doc.getDouble("price");
        if (priceVal == null) {
            Long priceLong = doc.getLong("price");
            priceVal = priceLong != null ? priceLong.doubleValue() : 0.0;
        }
        p.setPrice(priceVal);

        Double tablePriceVal = doc.getDouble("tablePrice");
        if (tablePriceVal == null) {
            Long tablePriceLong = doc.getLong("tablePrice");
            tablePriceVal = tablePriceLong != null ? tablePriceLong.doubleValue() : 0.0;
        }
        p.setTablePrice(tablePriceVal);

        Double foodPriceVal = doc.getDouble("foodPrice");
        if (foodPriceVal == null) {
            Long foodPriceLong = doc.getLong("foodPrice");
            foodPriceVal = foodPriceLong != null ? foodPriceLong.doubleValue() : 0.0;
        }
        p.setFoodPrice(foodPriceVal);

        p.setPaymentMethod(doc.getString("paymentMethod"));
        p.setStatus(doc.getString("status"));

        return p;
    }
}
