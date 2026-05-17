package com.example.billards.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.Models.BillardTable;
import com.example.billards.Models.ManageProductAdapter;
import com.example.billards.Models.ManageTableAdapter;
import com.example.billards.Models.Product;
import com.example.billards.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TableProductManagementFragment extends Fragment {

    private TabLayout tabLayout;
    private View layoutTableManagement, layoutProductManagement;

    // Table Management Views & Adapters
    private RecyclerView rvTables;
    private ManageTableAdapter tableAdapter;
    private List<BillardTable> tableList;

    // Product Management Views & Adapters
    private RecyclerView rvProducts;
    private ManageProductAdapter productAdapter;
    private List<Product> productList;

    private FirebaseFirestore db;

    // Image Picker Constants & State
    private static final int PICK_IMAGE_REQUEST = 2020;
    private String selectedImageBase64 = "";
    private ImageView imgProductPreview;

    public TableProductManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_table_product_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // 1. Bind tab toggles
        tabLayout = view.findViewById(R.id.tabLayoutTableProduct);
        layoutTableManagement = view.findViewById(R.id.layoutTableManagement);
        layoutProductManagement = view.findViewById(R.id.layoutProductManagement);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    layoutTableManagement.setVisibility(View.VISIBLE);
                    layoutProductManagement.setVisibility(View.GONE);
                } else {
                    layoutTableManagement.setVisibility(View.GONE);
                    layoutProductManagement.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 2. Set up Table Management RecyclerView
        rvTables = view.findViewById(R.id.rvTables);
        tableList = new ArrayList<>();
        tableAdapter = new ManageTableAdapter(tableList, getContext(), new ManageTableAdapter.OnTableActionListener() {
            @Override
            public void onEdit(BillardTable table) {
                showEditTableDialog(table);
            }

            @Override
            public void onDelete(BillardTable table) {
                showDeleteTableConfirmDialog(table);
            }
        });
        rvTables.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTables.setAdapter(tableAdapter);

        // Table FloatingActionButton (Thêm Bàn)
        view.findViewById(R.id.fabAddTable).setOnClickListener(v -> showAddTableDialog());

        // 3. Set up Product Management RecyclerView
        rvProducts = view.findViewById(R.id.rvProducts);
        productList = new ArrayList<>();
        productAdapter = new ManageProductAdapter(productList, getContext(), new ManageProductAdapter.OnProductActionListener() {
            @Override
            public void onEdit(Product product) {
                showEditProductDialog(product);
            }

            @Override
            public void onDelete(Product product) {
                showDeleteProductConfirmDialog(product);
            }
        });
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProducts.setAdapter(productAdapter);

        // Product FloatingActionButton (Thêm Món)
        view.findViewById(R.id.fabAddProduct).setOnClickListener(v -> showAddProductDialog());

        // 4. Start Firestore Sync
        loadTablesFromFirestore();
        loadProductsFromFirestore();
    }

    // ==========================================
    // FIRESTORE SYNC LOGIC
    // ==========================================

    private void loadTablesFromFirestore() {
        db.collection("table")
                .orderBy("number", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        tableList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            BillardTable table = doc.toObject(BillardTable.class);
                            if (table != null) {
                                table.setId(doc.getId());
                                tableList.add(table);
                            }
                        }
                        tableAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadProductsFromFirestore() {
        db.collection("products")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        productList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Product p = doc.toObject(Product.class);
                            if (p != null) {
                                p.setId(doc.getId());
                                productList.add(p);
                            }
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                });
    }

    // ==========================================
    // TABLE CRUD DIALOGS
    // ==========================================

    private void showAddTableDialog() {
        // Auto-detect the next number
        int maxNumber = 0;
        for (BillardTable t : tableList) {
            if (t.getnumber() > maxNumber) {
                maxNumber = t.getnumber();
            }
        }
        int nextNumber = maxNumber + 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm bàn chơi mới");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(nextNumber));
        input.setHint("Nhập số bàn");
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String numberStr = input.getText().toString().trim();
            if (numberStr.isEmpty()) {
                Toast.makeText(getContext(), "Số bàn không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int num = Integer.parseInt(numberStr);
                db.collection("table").whereEqualTo("number", num).get().addOnSuccessListener(snapshots -> {
                    if (!snapshots.isEmpty()) {
                        Toast.makeText(getContext(), "Bàn số " + num + " đã tồn tại!", Toast.LENGTH_SHORT).show();
                    } else {
                        String tableId = db.collection("table").document().getId();
                        BillardTable newTable = new BillardTable();
                        newTable.setId(tableId);
                        newTable.setNumber(num);
                        newTable.setPlaying(false);
                        newTable.setStartTime(0);

                        db.collection("table").document(tableId).set(newTable)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Thêm bàn số " + num + " thành công!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Thêm bàn thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Số bàn không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showEditTableDialog(BillardTable table) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chỉnh sửa số bàn");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(table.getnumber()));
        builder.setView(input);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String numberStr = input.getText().toString().trim();
            if (numberStr.isEmpty()) {
                Toast.makeText(getContext(), "Số bàn không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int newNum = Integer.parseInt(numberStr);
                if (newNum == table.getnumber()) return; // No change

                db.collection("table").whereEqualTo("number", newNum).get().addOnSuccessListener(snapshots -> {
                    if (!snapshots.isEmpty()) {
                        Toast.makeText(getContext(), "Bàn số " + newNum + " đã tồn tại!", Toast.LENGTH_SHORT).show();
                    } else {
                        db.collection("table").document(table.getId()).update("number", newNum)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Cập nhật số bàn thành công!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Số bàn không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showDeleteTableConfirmDialog(BillardTable table) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa bàn")
                .setMessage("Bạn có chắc chắn muốn xóa Bàn số " + table.getnumber() + " không?")
                .setPositiveButton("Xóa bàn", (dialog, which) -> {
                    db.collection("table").document(table.getId()).delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã xóa bàn thành công!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Xóa bàn thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ==========================================
    // PRODUCT CRUD DIALOGS
    // ==========================================

    private void showAddProductDialog() {
        selectedImageBase64 = "";

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_product, null);
        imgProductPreview = dialogView.findViewById(R.id.imgProductPreview);
        MaterialButton btnChoose = dialogView.findViewById(R.id.btnChooseProductPhoto);
        EditText etName = dialogView.findViewById(R.id.etProductName);
        EditText etPrice = dialogView.findViewById(R.id.etProductPrice);

        btnChoose.setOnClickListener(v -> openGallery());

        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setTitle("Thêm Đồ Ăn / Thức Uống")
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr)) {
                        Toast.makeText(getContext(), "Không được để trống thông tin!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int price = Integer.parseInt(priceStr);
                        String newId = db.collection("products").document().getId();

                        Product newProduct = new Product();
                        newProduct.setId(newId);
                        newProduct.setName(name);
                        newProduct.setPrice(price);
                        newProduct.setImageResId(0);
                        newProduct.setImageBase64(selectedImageBase64);

                        db.collection("products").document(newId).set(newProduct)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Thêm món " + name + " thành công!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi thêm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Giá bán không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditProductDialog(Product product) {
        selectedImageBase64 = product.getImageBase64(); // Prefill base64 image

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_product, null);
        imgProductPreview = dialogView.findViewById(R.id.imgProductPreview);
        MaterialButton btnChoose = dialogView.findViewById(R.id.btnChooseProductPhoto);
        EditText etName = dialogView.findViewById(R.id.etProductName);
        EditText etPrice = dialogView.findViewById(R.id.etProductPrice);

        // Pre-populate fields
        etName.setText(product.getName());
        etPrice.setText(String.valueOf(product.getPrice()));

        // Display current photo
        if (product.getImageBase64() != null && !product.getImageBase64().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(product.getImageBase64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgProductPreview.setImageBitmap(decodedByte);
            } catch (Exception e) {
                imgProductPreview.setImageResource(R.drawable.coca);
            }
        } else {
            imgProductPreview.setImageResource(product.getImageResId() != 0 ? product.getImageResId() : R.drawable.coca);
        }

        btnChoose.setOnClickListener(v -> openGallery());

        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setTitle("Chỉnh sửa món ăn / thức uống")
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr)) {
                        Toast.makeText(getContext(), "Không được để trống thông tin!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int price = Integer.parseInt(priceStr);
                        product.setName(name);
                        product.setPrice(price);
                        product.setImageBase64(selectedImageBase64);

                        db.collection("products").document(product.getId()).set(product)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Giá bán không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteProductConfirmDialog(Product product) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa món")
                .setMessage("Bạn có chắc chắn muốn xóa món " + product.getName() + " không?")
                .setPositiveButton("Xóa món", (dialog, which) -> {
                    db.collection("products").document(product.getId()).delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã xóa món ăn thành công!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Xóa món thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ==========================================
    // IMAGE PICKER LOGIC
    // ==========================================

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            if (imgProductPreview != null && getActivity() != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    Bitmap scaled = scaleBitmapDown(bitmap, 300);
                    imgProductPreview.setImageBitmap(scaled);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    byte[] b = baos.toByteArray();
                    selectedImageBase64 = Base64.encodeToString(b, Base64.DEFAULT);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        if (originalWidth > maxDimension || originalHeight > maxDimension) {
            if (originalWidth > originalHeight) {
                newWidth = maxDimension;
                newHeight = (int) (maxDimension * ((float) originalHeight / (float) originalWidth));
            } else if (originalHeight > originalWidth) {
                newHeight = maxDimension;
                newWidth = (int) (maxDimension * ((float) originalWidth / (float) originalHeight));
            } else {
                newWidth = maxDimension;
                newHeight = maxDimension;
            }
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
