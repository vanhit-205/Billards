package com.example.billards.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.Fragment.OrderFragment;
import com.example.billards.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private OrderFragment orderFragment;

    public ProductAdapter(List<Product> productList, Context context, OrderFragment orderFragment) {
        this.productList = productList;
        this.context = context;
        this.orderFragment = orderFragment;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getName());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvProductPrice.setText(formatter.format(product.getPrice()));


        holder.imgProduct.setImageResource(product.getImageResId());

        holder.btnMinus.setOnClickListener(view -> {
            int quantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            if (quantity > 1) {
                quantity--;
                holder.tvQuantity.setText(String.valueOf(quantity));
            }
        });
        holder.btnAdd.setOnClickListener(view -> {
            int quantity = Integer.parseInt(holder.tvQuantity.getText().toString());
                quantity++;
                holder.tvQuantity.setText(String.valueOf(quantity));
        });

        holder.btnOrder.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            orderFragment.addToCart(product, quantity);
            holder.tvQuantity.setText("1"); // Reset quantity
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice;
        Button btnOrder;
        TextView btnMinus, btnAdd;
        TextView tvQuantity;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            btnOrder = itemView.findViewById(R.id.btnOrder);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}