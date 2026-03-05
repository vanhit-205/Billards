package com.example.billards.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder>{

    private List<BillardTable> tableList;
    private Context context;

    public TableAdapter(List<BillardTable> tableList,Context context){
        this.tableList=tableList;
        this.context=context;
    }
    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(context).inflate(R.layout.fragment_item_table,parent,false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        BillardTable table=tableList.get(position);

        holder.tvTableNumber.setText(String.valueOf(table.getnumber()));

        if(table.getisPlaying()){
            holder.btnStart.setText("Tính tiền");
            holder.btnStart.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_red_dark));

            long diff = System.currentTimeMillis() - table.getStartTime();
            holder.tvPlayTime.setText(formatTime(diff));
        }   else{
            holder.btnStart.setText("Bắt đầu");
            holder.btnStart.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_blue_light));
            holder.tvPlayTime.setText("00:00:00");
        }

        holder.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Tables").child(table.getId());
                if (!table.getisPlaying()) {
                    dbRef.child("isPlaying").setValue(true);
                    dbRef.child("startTime").setValue(System.currentTimeMillis());
                    Toast.makeText(context, "Đã bắt đầu bàn " + table.getnumber(), Toast.LENGTH_SHORT).show();
                } else {
                    payment(table, dbRef);
                }
            }
        });

    }

    private String formatTime(long millis){
        int seconds=(int) ((millis/1000)%60);
        int minutes=(int) ((millis/1000*60)%60);
        int hours=(int) ((millis/1000*60*60));
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void payment(BillardTable table, DatabaseReference  dbRef){
        long time= System.currentTimeMillis()-table.getStartTime();
        long hours=time/(1000*60*60);
        double total=(int)hours*50000;

        Toast.makeText(context, "Bàn " + table.getnumber() + " thanh toán: " + Math.round(total) + "đ", Toast.LENGTH_LONG).show();

        dbRef.child("isPlaying").setValue(false);
        dbRef.child("startTime").setValue(0);
    }

    @Override
    public int getItemCount() {
        return tableList != null ? tableList.size() : 0;
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView tvTableNumber, tvPlayTime;
        Button btnStart;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTableNumber = itemView.findViewById(R.id.tvTableNumber);
            tvPlayTime = itemView.findViewById(R.id.tvPlayTime);
            btnStart = itemView.findViewById(R.id.btnStart);
        }
    }
}