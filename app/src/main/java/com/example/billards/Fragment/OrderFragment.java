package com.example.billards.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.billards.Models.BillardTable;
import com.example.billards.Models.Product;
import com.example.billards.Models.ProductAdapter;
import com.example.billards.R;

import java.util.ArrayList;
import java.util.List;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.billards.Models.Orders;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderFragment extends Fragment {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Orders> localCart = new ArrayList<>();
    private TextView tvTempTotal;
    private Button btnConfirmOrder;

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
        tvTempTotal = view.findViewById(R.id.tv_temp_total);
        btnConfirmOrder = view.findViewById(R.id.btn_confirm_order);
        Button btnClearCart = view.findViewById(R.id.btn_clear_cart);

        initData();

        productAdapter = new ProductAdapter(productList, getContext(), this);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvProducts.setAdapter(productAdapter);

        btnConfirmOrder.setOnClickListener(v -> showTableSelectionDialog());

        btnClearCart.setOnClickListener(v -> {
            if (localCart.isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng đã trống rồi!", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(getContext())
                .setTitle("Xóa giỏ hàng")
                .setMessage("Bạn có chắc chắn muốn xóa trống tất cả món trong giỏ hàng?")
                .setPositiveButton("Xóa trống", (dialog, which) -> {
                    localCart.clear();
                    updateTotal();
                    Toast.makeText(getContext(), "Đã xóa trống giỏ hàng!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
        });

        updateTotal();
    }

    private void initData() {
        productList = new ArrayList<>();
        loadProductsFromFirestore();
    }

    private void loadProductsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").get()
            .addOnSuccessListener(querySnapshot -> {
                productList.clear();
                if (querySnapshot.isEmpty()) {
                    writeDefaultProductsToFirestore(db);
                } else {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Product p = doc.toObject(Product.class);
                        if (p != null) {
                            p.setId(doc.getId());
                            productList.add(p);
                        }
                    }
                    if (productAdapter != null) {
                        productAdapter.notifyDataSetChanged();
                    }
                }
            })
            .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi tải sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void writeDefaultProductsToFirestore(FirebaseFirestore db) {
        List<Product> defaults = new ArrayList<>();
        defaults.add(new Product("Bò húc", 15000, R.drawable.bohuc));
        defaults.add(new Product("Coca Cola", 10000, R.drawable.coca));
        defaults.add(new Product("Trà xanh 0 độ", 12000, R.drawable.khongdo));
        defaults.add(new Product("Mì tôm", 20000, R.drawable.mitom));

        WriteBatch batch = db.batch();
        for (Product p : defaults) {
            DocumentReference ref = db.collection("products").document();
            p.setId(ref.getId());
            batch.set(ref, p);
        }
        batch.commit().addOnSuccessListener(aVoid -> {
            loadProductsFromFirestore();
        });
    }

    public void addToCart(Product product, int quantity) {
        // Check if product already in cart
        for (Orders order : localCart) {
            if (order.getPrice() == product.getPrice()) { // Assuming unique by price for simplicity; better to add productID
                order.setQuantity(order.getQuantity() + quantity);
                updateTotal();
                return;
            }
        }
        // Add new
        Orders newOrder = new Orders(0, product.getPrice(), quantity); // tableID set later
        localCart.add(newOrder);
        updateTotal();
    }

    private void updateTotal() {
        int total = 0;
        for (Orders order : localCart) {
            total += order.getItemTotal();
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTempTotal.setText("Tổng tạm tính: " + formatter.format(total));
    }

    private void showTableSelectionDialog() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("table")
            .whereEqualTo("isPlaying", true)
            .orderBy("number")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<BillardTable> tables = querySnapshot.toObjects(BillardTable.class);
                if (tables.isEmpty()) {
                    Toast.makeText(getContext(), "Không có bàn đang chơi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] tableNumbers = new String[tables.size()];
                for (int i = 0; i < tables.size(); i++) {
                    tableNumbers[i] = "Bàn " + tables.get(i).getnumber();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Chọn bàn")
                    .setItems(tableNumbers, (dialog, which) -> {
                        int selectedTableNumber = tables.get(which).getnumber();
                        saveOrdersToFirestore(selectedTableNumber);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            })
            .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi lấy danh sách bàn: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveOrdersToFirestore(int tableID) {
        if (localCart.isEmpty()) {
            Toast.makeText(getContext(), "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        for (Orders order : localCart) {
            order.setTableID(tableID);
            DocumentReference ref = db.collection("orders").document();
            order.setId(ref.getId());
            batch.set(ref, order);
        }

        batch.commit()
            .addOnSuccessListener(task -> {
                Toast.makeText(getContext(), "Đặt món thành công cho bàn " + tableID, Toast.LENGTH_SHORT).show();
                localCart.clear();
                updateTotal();
            })
            .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi lưu đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}