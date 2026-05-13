package com.example.billards.Models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.R;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    private List<Users> staffList;
    private OnStaffDeleteListener deleteListener;

    public interface OnStaffDeleteListener {
        void onDelete(Users user);
    }

    public StaffAdapter(List<Users> staffList, OnStaffDeleteListener deleteListener) {
        this.staffList = staffList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        Users user = staffList.get(position);
        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(user));
    }

    @Override
    public int getItemCount() {
        return staffList != null ? staffList.size() : 0;
    }

    public static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;
        ImageButton btnDelete;

        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStaffName);
            tvEmail = itemView.findViewById(R.id.tvStaffEmail);
            btnDelete = itemView.findViewById(R.id.btnDeleteStaff);
        }
    }
}
