package com.example.billards.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.Models.BillardTable;
import com.example.billards.Models.TableAdapter;
import com.example.billards.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PaymentFragment extends Fragment {

    private RecyclerView rvTablesPayment;
    private TableAdapter adapter;
    private List<BillardTable> tableList;
    private DatabaseReference mDatabase;

    public PaymentFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment, container, false);


        rvTablesPayment = view.findViewById(R.id.rvTablesPayment);


        tableList = new ArrayList<>();

        rvTablesPayment.setLayoutManager(new GridLayoutManager(getContext(), 2));


        adapter = new TableAdapter(tableList, getContext());
        rvTablesPayment.setAdapter(adapter);


        mDatabase = FirebaseDatabase.getInstance().getReference("Tables");


        loadDataFromFirebase();

        return view;
    }

    private void loadDataFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tableList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    BillardTable table = data.getValue(BillardTable.class);
                    if (table != null) {
                        tableList.add(table);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}