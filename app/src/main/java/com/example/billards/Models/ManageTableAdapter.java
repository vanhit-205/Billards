package com.example.billards.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.R;

import java.util.List;

public class ManageTableAdapter extends RecyclerView.Adapter<ManageTableAdapter.TableViewHolder> {

    private List<BillardTable> tableList;
    private Context context;
    private OnTableActionListener actionListener;

    public interface OnTableActionListener {
        void onEdit(BillardTable table);
        void onDelete(BillardTable table);
    }

    public ManageTableAdapter(List<BillardTable> tableList, Context context, OnTableActionListener actionListener) {
        this.tableList = tableList;
        this.context = context;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        BillardTable table = tableList.get(position);

        holder.tvTableNumberCircle.setText(String.valueOf(table.getnumber()));
        holder.tvTableName.setText("Bàn số " + table.getnumber());

        if (table.getisPlaying()) {
            holder.tvTableStatus.setText("Đang sử dụng");
            holder.tvTableStatus.setTextColor(context.getResources().getColor(R.color.destructive));
        } else {
            holder.tvTableStatus.setText("Trống");
            holder.tvTableStatus.setTextColor(0xFF10B981); // Green color code
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onEdit(table);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onDelete(table);
        });
    }

    @Override
    public int getItemCount() {
        return tableList != null ? tableList.size() : 0;
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView tvTableNumberCircle, tvTableName, tvTableStatus;
        ImageButton btnEdit, btnDelete;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTableNumberCircle = itemView.findViewById(R.id.tvTableNumberCircle);
            tvTableName = itemView.findViewById(R.id.tvTableName);
            tvTableStatus = itemView.findViewById(R.id.tvTableStatus);
            btnEdit = itemView.findViewById(R.id.btnEditTable);
            btnDelete = itemView.findViewById(R.id.btnDeleteTable);
        }
    }
}
