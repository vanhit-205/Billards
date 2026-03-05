package com.example.billards.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.Models.BillardTable;
import com.example.billards.Models.TableAdapter;
import com.example.billards.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PaymentFragment extends Fragment {

    private RecyclerView rvTablesPayment;
    private TableAdapter adapter;
    private List<BillardTable> tableList;
    private FirebaseFirestore db;

    public PaymentFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment, container, false);


        rvTablesPayment = view.findViewById(R.id.rvTablesPayment);


        tableList = new ArrayList<>();

        rvTablesPayment.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter = new TableAdapter(tableList, getContext());
        rvTablesPayment.setAdapter(adapter);


        db = FirebaseFirestore.getInstance();
        loadDataFromFirestore();

        return view;
    }

    private void loadDataFromFirestore() {
        db.collection("table").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("DEBUG_FIRESTORE", "Lỗi: " + error.getMessage());
                return;
            }

            if (value != null) {
                tableList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    // Firestore tự động chuyển document thành object Java
                    BillardTable table = doc.toObject(BillardTable.class);
                    table.setId(doc.getId());
                    tableList.add(table);
                }
                Log.d("DEBUG_FIRESTORE", "Số lượng bàn: " + tableList.size());
                adapter.notifyDataSetChanged();
            }
        });
    }
}