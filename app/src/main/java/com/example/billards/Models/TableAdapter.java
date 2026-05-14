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

        db.collection("orders").whereEqualTo("tableID", table.getnumber()).get().addOnSuccessListener(querySnapshot -> {
            long ordersTotal = 0;
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                ordersTotal += doc.getLong("price") * doc.getLong("quantity");
            }
            long totalAmount = timePrice + ordersTotal;

            View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_payment_choice, null);
            AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();

            dialogView.findViewById(R.id.btnCash).setOnClickListener(v -> {
                processPayment(table, tableRef, diff, totalAmount, "cash", "completed");
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

                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, 999);
                }
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    public void processPayment(BillardTable table, DocumentReference tableRef, long diff, long total, String method, String status) {
        DocumentReference paymentRef = db.collection("payments").document();
        Payment payment = new Payment(paymentRef.getId(), table.getnumber(), System.currentTimeMillis(), diff, total, method, status);
        
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
