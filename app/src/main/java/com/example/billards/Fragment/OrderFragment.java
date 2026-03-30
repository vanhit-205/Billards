package com.example.billards.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.billards.Models.Product;
import com.example.billards.Models.ProductAdapter;
import com.example.billards.R;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    public OrderFragment() {
        // Required empty public constructor
    }

    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvProducts = view.findViewById(R.id.rvProducts);
        
        // Khởi tạo dữ liệu mẫu
        initData();

        // Thiết lập RecyclerView với 2 cột (Grid)
        productAdapter = new ProductAdapter(productList, getContext());
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvProducts.setAdapter(productAdapter);
    }

    private void initData() {
        productList = new ArrayList<>();
        

        productList.add(new Product("Bò húc", 15000, R.drawable.bohuc));
        productList.add(new Product("Coca Cola", 10000, R.drawable.coca));
        productList.add(new Product("Trà xanh 0 độ", 12000, R.drawable.khongdo));
        productList.add(new Product("Mì tôm", 20000, R.drawable.mitom));
    }
}