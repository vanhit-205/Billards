package com.example.billards.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.R;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.HistoryViewHolder> {

    private List<Payment> paymentList;
    private Context context;
    private NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public PaymentHistoryAdapter(List<Payment> paymentList, Context context) {
        this.paymentList = paymentList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Payment payment = paymentList.get(position);

        holder.tvTableNumber.setText(String.valueOf(payment.getTable()));
        holder.tvBillTitle.setText("Hóa đơn bàn " + payment.getTable());
        holder.tvBillTime.setText(dateFormat.format(new Date(payment.getTime())));
        holder.tvBillAmount.setText(currencyFormat.format(payment.getPrice()) + " đ");

        String method = payment.getPaymentMethod();
        if ("cash".equalsIgnoreCase(method)) {
            holder.tvBillMethod.setText("Tiền mặt");
        } else if ("vnpay".equalsIgnoreCase(method)) {
            holder.tvBillMethod.setText("VNPAY QR");
        } else if (method == null || method.isEmpty()) {
            holder.tvBillMethod.setText("Tiền mặt");
        } else {
            holder.tvBillMethod.setText(method);
        }

        holder.itemView.setOnClickListener(v -> showBillDetailDialog(payment));
    }

    private void showBillDetailDialog(Payment payment) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_bill_detail, null);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();

        TextView tvTableNumber = dialogView.findViewById(R.id.tvTableNumber);
        TextView tvPlayTime = dialogView.findViewById(R.id.tvPlayTime);
        TextView tvPaymentTime = dialogView.findViewById(R.id.tvPaymentTime);
        TextView tvPaymentMethod = dialogView.findViewById(R.id.tvPaymentMethod);
        TextView tvTimePrice = dialogView.findViewById(R.id.tvTimePrice);
        TextView tvFoodPrice = dialogView.findViewById(R.id.tvFoodPrice);
        TextView tvTotalAmount = dialogView.findViewById(R.id.tvTotalAmount);
        MaterialButton btnClose = dialogView.findViewById(R.id.btnClose);

        tvTableNumber.setText("Bàn " + payment.getTable());
        tvPlayTime.setText(formatDuration(payment.getTimePlay()));
        tvPaymentTime.setText(dateFormat.format(new Date(payment.getTime())));
        
        String method = payment.getPaymentMethod();
        if ("cash".equalsIgnoreCase(method)) {
            tvPaymentMethod.setText("Tiền mặt");
        } else if ("vnpay".equalsIgnoreCase(method)) {
            tvPaymentMethod.setText("VNPAY QR");
        } else if (method == null || method.isEmpty()) {
            tvPaymentMethod.setText("Tiền mặt");
        } else {
            tvPaymentMethod.setText(method);
        }

        tvTimePrice.setText(currencyFormat.format(payment.getTablePrice()) + " đ");
        tvFoodPrice.setText(currencyFormat.format(payment.getFoodPrice()) + " đ");
        tvTotalAmount.setText(currencyFormat.format(payment.getPrice()) + " đ");

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private String formatDuration(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60));
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTableNumber, tvBillTitle, tvBillTime, tvBillMethod, tvBillAmount;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTableNumber = itemView.findViewById(R.id.tvTableNumber);
            tvBillTitle = itemView.findViewById(R.id.tvBillTitle);
            tvBillTime = itemView.findViewById(R.id.tvBillTime);
            tvBillMethod = itemView.findViewById(R.id.tvBillMethod);
            tvBillAmount = itemView.findViewById(R.id.tvBillAmount);
        }
    }
}
