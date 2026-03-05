package com.example.billards.Models;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder>{

    private List<BillardTable> tableList;
    private Context context;

    private Handler timeHandler = new Handler(Looper.getMainLooper());
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Khởi tạo dùng chung

    public TableAdapter(List<BillardTable> tableList, Context context){
        this.tableList = tableList;
        this.context = context;
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

        if(table.getisPlaying()){
            holder.btnStart.setText("Tính tiền");
            holder.btnStart.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_red_dark));

            holder.updateTimerRunnable = new Runnable() {
                @Override
                public void run() {
                    long diff = System.currentTimeMillis() - table.getStartTime();
                    holder.tvPlayTime.setText(formatTime(diff));
                    timeHandler.postDelayed(this, 1000); // Lặp lại sau mỗi 1 giây
                }
            };
            timeHandler.post(holder.updateTimerRunnable);
        } else {
            holder.btnStart.setText("Bắt đầu");
            holder.btnStart.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_blue_light));
            holder.tvPlayTime.setText("00:00:00");
        }

        holder.btnStart.setOnClickListener(view -> {
            DocumentReference tableRef = db.collection("table").document(table.getId());
            if (!table.getisPlaying()) {
                tableRef.update("isPlaying", true, "startTime", System.currentTimeMillis())
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Bắt đầu bàn " + table.getnumber(), Toast.LENGTH_SHORT).show());
            } else {
                payment(table, tableRef);
            }
        });
    }

    private String formatTime(long millis){
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours   = (millis / (1000 * 60 * 60));
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void payment(BillardTable table, DocumentReference tableRef){
        long diff = System.currentTimeMillis() - table.getStartTime();

        double minutes = diff / (1000.0 * 60);
        double pricePerMinute = 50000.0 / 60.0;
        long total = (long) (minutes * pricePerMinute);

        String msg = "Bàn " + table.getnumber() + " chơi " + Math.round(minutes) + " phút. Tổng: " + total + "đ";


        tableRef.update("isPlaying", false, "startTime", 0)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show());
    }

    @Override
    public int getItemCount() {
        return tableList != null ? tableList.size() : 0;
    }

    public void stopAllTimers() {
        timeHandler.removeCallbacksAndMessages(null);
    }
    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView tvTableNumber, tvPlayTime;
        Button btnStart;

        Runnable updateTimerRunnable;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTableNumber = itemView.findViewById(R.id.tvTableNumber);
            tvPlayTime = itemView.findViewById(R.id.tvPlayTime);
            btnStart = itemView.findViewById(R.id.btnStart);
        }
    }
}