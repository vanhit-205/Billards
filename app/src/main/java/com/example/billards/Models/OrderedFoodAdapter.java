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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderedFoodAdapter extends RecyclerView.Adapter<OrderedFoodAdapter.OrderedViewHolder> {

    private List<Orders> orderedList;
    private Context context;
    private OnItemCancelListener cancelListener;
    private NumberFormat currencyFormat;

    public interface OnItemCancelListener {
        void onCancel(Orders order);
    }

    public OrderedFoodAdapter(List<Orders> orderedList, Context context, OnItemCancelListener cancelListener) {
        this.orderedList = orderedList;
        this.context = context;
        this.cancelListener = cancelListener;
        this.currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public OrderedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ordered_food, parent, false);
        return new OrderedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderedViewHolder holder, int position) {
        Orders order = orderedList.get(position);
        
        String foodName = getProductNameByPrice(order.getPrice());
        holder.tvFoodName.setText(foodName);
        
        holder.tvQuantityPrice.setText(order.getQuantity() + " x " + currencyFormat.format(order.getPrice()) + " đ");
        holder.tvItemTotal.setText(currencyFormat.format(order.getItemTotal()) + " đ");
        
        holder.btnCancel.setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.onCancel(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderedList != null ? orderedList.size() : 0;
    }

    private String getProductNameByPrice(int price) {
        switch (price) {
            case 15000: return "Bò húc";
            case 10000: return "Coca Cola";
            case 12000: return "Trà xanh 0 độ";
            case 20000: return "Mì tôm";
            default: return "Đồ ăn / Nước uống";
        }
    }

    public static class OrderedViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvQuantityPrice, tvItemTotal;
        ImageButton btnCancel;

        public OrderedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvQuantityPrice = itemView.findViewById(R.id.tvQuantityPrice);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
            btnCancel = itemView.findViewById(R.id.btnCancelItem);
        }
    }
}
