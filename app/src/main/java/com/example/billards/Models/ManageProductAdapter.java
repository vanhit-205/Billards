package com.example.billards.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ManageProductAdapter extends RecyclerView.Adapter<ManageProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private OnProductActionListener actionListener;
    private NumberFormat currencyFormat;

    public interface OnProductActionListener {
        void onEdit(Product product);
        void onDelete(Product product);
    }

    public ManageProductAdapter(List<Product> productList, Context context, OnProductActionListener actionListener) {
        this.productList = productList;
        this.context = context;
        this.actionListener = actionListener;
        this.currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(currencyFormat.format(product.getPrice()) + " đ");

        // Load image base64 or fallback to drawable resource ID
        if (product.getImageBase64() != null && !product.getImageBase64().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(product.getImageBase64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (decodedByte != null) {
                    holder.imgProductPhoto.setImageBitmap(decodedByte);
                } else {
                    holder.imgProductPhoto.setImageResource(R.drawable.coca);
                }
            } catch (Exception e) {
                holder.imgProductPhoto.setImageResource(R.drawable.coca);
            }
        } else {
            holder.imgProductPhoto.setImageResource(product.getImageResId() != 0 ? product.getImageResId() : R.drawable.coca);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onEdit(product);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onDelete(product);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProductPhoto;
        TextView tvProductName, tvProductPrice;
        ImageButton btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProductPhoto = itemView.findViewById(R.id.imgProductPhoto);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            btnEdit = itemView.findViewById(R.id.btnEditProduct);
            btnDelete = itemView.findViewById(R.id.btnDeleteProduct);
        }
    }
}
