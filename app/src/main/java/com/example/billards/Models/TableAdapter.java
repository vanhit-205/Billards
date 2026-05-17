package com.example.billards.Models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.Activities.VNPayActivity;
import com.example.billards.R;
import com.example.billards.utils.VNPayHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.billards.Models.Orders;
import com.example.billards.Models.OrderedFoodAdapter;

import java.util.List;
import java.util.Locale;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private List<BillardTable> tableList;
    private Context context;
    private Handler timeHandler = new Handler(Looper.getMainLooper());
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TableAdapter(List<BillardTable> tableList, Context context) {
        this.tableList = tableList;
        this.context = context;
        setupFirestoreListener();
    }

    private void setupFirestoreListener() {
        db.collection("orders").addSnapshotListener((snapshot, e) -> {
            if (e != null) return;
            if (snapshot != null) notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        BillardTable table = tableList.get(position);
        holder.tvTableNumber.setText(String.valueOf(table.getnumber()));
        timeHandler.removeCallbacks(holder.updateTimerRunnable);

        if (table.getisPlaying()) {
            holder.btnStart.setText("Tính tiền");
            holder.btnStart.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.destructive)));
            holder.updateTimerRunnable = new Runnable() {
                @Override
                public void run() {
                    long diff = System.currentTimeMillis() - table.getStartTime();
                    holder.tvPlayTime.setText(formatTime(diff));
                    timeHandler.postDelayed(this, 1000);
                }
            };
            timeHandler.post(holder.updateTimerRunnable);
        } else {
            holder.btnStart.setText("Bắt đầu");
            holder.btnStart.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_600)));
            holder.tvPlayTime.setText("00:00:00");
        }

        holder.btnStart.setOnClickListener(view -> {
            DocumentReference tableRef = db.collection("table").document(table.getId());
            if (!table.getisPlaying()) {
                tableRef.update("isPlaying", true, "startTime", System.currentTimeMillis());
            } else {
                showPaymentMethodDialog(table, tableRef);
            }
        });
    }

    private void showPaymentMethodDialog(BillardTable table, DocumentReference tableRef) {
        long diff = System.currentTimeMillis() - table.getStartTime();
        double minutes = diff / (1000.0 * 60);
        long timePrice = (long) (minutes * (50000.0 / 60.0));

        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_payment_choice, null);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();

        refreshPaymentChoiceDialog(dialogView, table, timePrice, diff, tableRef, dialog);

        dialog.show();
    }

    private void refreshPaymentChoiceDialog(View dialogView, BillardTable table, long timePrice, long diff, DocumentReference tableRef, AlertDialog dialog) {
        db.collection("orders").whereEqualTo("tableID", table.getnumber()).get().addOnSuccessListener(querySnapshot -> {
            long calculatedOrdersTotal = 0;
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                calculatedOrdersTotal += doc.getLong("price") * doc.getLong("quantity");
            }
            final long ordersTotal = calculatedOrdersTotal;
            final long totalAmount = timePrice + ordersTotal;

            java.text.NumberFormat currencyFormat = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));

            TextView tvTableNumber = dialogView.findViewById(R.id.tvTableNumber);
            TextView tvTimePrice = dialogView.findViewById(R.id.tvTimePrice);
            TextView tvFoodPrice = dialogView.findViewById(R.id.tvFoodPrice);
            TextView tvTotalAmount = dialogView.findViewById(R.id.tvTotalAmount);

            tvTableNumber.setText("Bàn " + table.getnumber());
            tvTimePrice.setText(currencyFormat.format(timePrice) + " đ");
            tvFoodPrice.setText(currencyFormat.format(ordersTotal) + " đ");
            tvTotalAmount.setText(currencyFormat.format(totalAmount) + " đ");

            dialogView.findViewById(R.id.btnCash).setOnClickListener(v -> {
                processPayment(table, tableRef, diff, (double) timePrice, (double) ordersTotal, totalAmount, "cash", "completed");
                dialog.dismiss();
            });

            dialogView.findViewById(R.id.btnVNPay).setOnClickListener(v -> {
                String invoiceId = "BILL" + System.currentTimeMillis();
                String paymentUrl = VNPayHelper.generateVNPayUrl(totalAmount, invoiceId);

                Intent intent = new Intent(context, VNPayActivity.class);
                intent.putExtra("PAYMENT_URL", paymentUrl);
                intent.putExtra("TABLE_ID", table.getId());
                intent.putExtra("TABLE_NUMBER", table.getnumber());
                intent.putExtra("TOTAL_AMOUNT", totalAmount);
                intent.putExtra("DIFF", diff);
                intent.putExtra("TABLE_PRICE", (double) timePrice);
                intent.putExtra("FOOD_PRICE", (double) ordersTotal);

                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, 999);
                }
                dialog.dismiss();
            });

            // Bind click listener for "Xem/Hủy món đã gọi"
            View layoutManageFood = dialogView.findViewById(R.id.layoutManageFood);
            TextView btnManageFood = dialogView.findViewById(R.id.btnManageFood);
            if (ordersTotal > 0) {
                layoutManageFood.setVisibility(View.VISIBLE);
                btnManageFood.setOnClickListener(v -> showManageOrdersDialog(table, () -> {
                    // Update main bill popup on manage popup close
                    refreshPaymentChoiceDialog(dialogView, table, timePrice, diff, tableRef, dialog);
                }));
            } else {
                layoutManageFood.setVisibility(View.GONE);
            }
        });
    }

    private void showManageOrdersDialog(BillardTable table, Runnable onClose) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_manage_orders, null);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();

        androidx.recyclerview.widget.RecyclerView rvOrderedItems = dialogView.findViewById(R.id.rvOrderedItems);
        TextView tvNoOrderedItems = dialogView.findViewById(R.id.tvNoOrderedItems);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        View btnBack = dialogView.findViewById(R.id.btnBack);

        tvTitle.setText("MÓN ĐÃ ĐẶT - BÀN " + table.getnumber());

        java.util.List<Orders> orderedList = new java.util.ArrayList<>();
        rvOrderedItems.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(context));
        
        final OrderedFoodAdapter[] adapterHolder = new OrderedFoodAdapter[1];
        OrderedFoodAdapter adapter = new OrderedFoodAdapter(orderedList, context, order -> {
            new AlertDialog.Builder(context)
                .setTitle("Hủy món ăn")
                .setMessage("Bạn có chắc chắn muốn hủy món này khỏi hóa đơn?")
                .setPositiveButton("Hủy món", (d, w) -> {
                    db.collection("orders").document(order.getId()).delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Đã hủy món thành công!", Toast.LENGTH_SHORT).show();
                            loadOrderedItems(table, orderedList, adapterHolder[0], tvNoOrderedItems, rvOrderedItems);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Lỗi khi hủy: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                })
                .setNegativeButton("Quay lại", null)
                .show();
        });
        adapterHolder[0] = adapter;
        rvOrderedItems.setAdapter(adapter);

        loadOrderedItems(table, orderedList, adapter, tvNoOrderedItems, rvOrderedItems);

        btnBack.setOnClickListener(v -> dialog.dismiss());
        dialog.setOnDismissListener(d -> {
            if (onClose != null) {
                onClose.run();
            }
        });

        dialog.show();
    }

    private void loadOrderedItems(BillardTable table, java.util.List<Orders> orderedList, OrderedFoodAdapter adapter, TextView tvNo, androidx.recyclerview.widget.RecyclerView rv) {
        db.collection("orders").whereEqualTo("tableID", table.getnumber()).get()
            .addOnSuccessListener(querySnapshot -> {
                orderedList.clear();
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    Orders order = doc.toObject(Orders.class);
                    if (order != null) {
                        order.setId(doc.getId());
                        orderedList.add(order);
                    }
                }
                
                if (orderedList.isEmpty()) {
                    tvNo.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                } else {
                    tvNo.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            });
    }

    public void processPayment(BillardTable table, DocumentReference tableRef, long diff, double tablePrice, double foodPrice, long total, String method, String status) {
        DocumentReference paymentRef = db.collection("payments").document();
        Payment payment = new Payment(paymentRef.getId(), table.getnumber(), System.currentTimeMillis(), diff, total, method, status, tablePrice, foodPrice);
        
        paymentRef.set(payment).addOnSuccessListener(aVoid -> {
            tableRef.update("isPlaying", false, "startTime", 0)
                    .addOnSuccessListener(unused -> {
                        // Xóa các order cũ của bàn này
                        db.collection("orders").whereEqualTo("tableID", table.getnumber()).get()
                                .addOnSuccessListener(snapshots -> {
                                    for (DocumentSnapshot doc : snapshots) doc.getReference().delete();
                                });
                        Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private String formatTime(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60));
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public int getItemCount() { return tableList.size(); }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView tvTableNumber, tvPlayTime;
        MaterialButton btnStart;
        Runnable updateTimerRunnable;
        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTableNumber = itemView.findViewById(R.id.tvTableNumber);
            tvPlayTime = itemView.findViewById(R.id.tvPlayTime);
            btnStart = itemView.findViewById(R.id.btnStart);
        }
    }
}
