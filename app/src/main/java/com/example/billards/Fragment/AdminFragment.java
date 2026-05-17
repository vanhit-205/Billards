package com.example.billards.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.billards.Activities.LoginScreen;
import com.example.billards.Models.UserSession;
import com.example.billards.Models.Users;
import com.example.billards.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import android.net.Uri;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AdminPagerAdapter adapter;
    private TextView tvAdminTitle;

    private static final int PICK_IMAGE_REQUEST = 1010;
    private String selectedImageBase64 = "";
    private android.widget.ImageView imgProductPreview;

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvAdminTitle = view.findViewById(R.id.tvAdminTitle);
        tabLayout = view.findViewById(R.id.adminTabLayout);
        viewPager = view.findViewById(R.id.adminViewPager);

        updateAdminName();

        tvAdminTitle.setOnClickListener(v -> showPopupMenu());

        // Quick action: Thêm bàn
        view.findViewById(R.id.btnAdminAddTable).setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("table").get().addOnSuccessListener(querySnapshot -> {
                int maxNumber = 0;
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    Long num = doc.getLong("number");
                    if (num != null && num > maxNumber) {
                        maxNumber = num.intValue();
                    }
                }
                int nextNumber = maxNumber + 1;

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Thêm bàn chơi mới");

                final EditText input = new EditText(getContext());
                input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
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
                                Toast.makeText(getContext(), "Số bàn " + num + " đã tồn tại!", Toast.LENGTH_SHORT).show();
                            } else {
                                String tableId = db.collection("table").document().getId();
                                com.example.billards.Models.BillardTable newTable = new com.example.billards.Models.BillardTable();
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
            });
        });

        // Quick action: Thêm sản phẩm
        view.findViewById(R.id.btnAdminAddProduct).setOnClickListener(v -> {
            selectedImageBase64 = "";

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_product, null);
            imgProductPreview = dialogView.findViewById(R.id.imgProductPreview);
            com.google.android.material.button.MaterialButton btnChoose = dialogView.findViewById(R.id.btnChooseProductPhoto);
            EditText etName = dialogView.findViewById(R.id.etProductName);
            EditText etPrice = dialogView.findViewById(R.id.etProductPrice);

            btnChoose.setOnClickListener(v2 -> openGallery());

            new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();

                    if (name.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(getContext(), "Không được để trống thông tin!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int price = Integer.parseInt(priceStr);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String newId = db.collection("products").document().getId();

                        com.example.billards.Models.Product newProduct = new com.example.billards.Models.Product();
                        newProduct.setId(newId);
                        newProduct.setName(name);
                        newProduct.setPrice(price);
                        newProduct.setImageResId(0);
                        newProduct.setImageBase64(selectedImageBase64);

                        db.collection("products").document(newId).set(newProduct)
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Thêm sản phẩm " + name + " thành công!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi thêm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Giá bán không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
        });

        adapter = new AdminPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Nhân viên");
                    tab.setIcon(R.drawable.ic_users);
                    break;
                case 1:
                    tab.setText("Thống kê");
                    tab.setIcon(R.drawable.ic_bar_chart);
                    break;
                case 2:
                    tab.setText("Lịch sử");
                    tab.setIcon(R.drawable.payment);
                    break;
            }
        }).attach();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == android.app.Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            if (imgProductPreview != null) {
                try {
                    android.graphics.Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    android.graphics.Bitmap scaled = scaleBitmapDown(bitmap, 300);
                    imgProductPreview.setImageBitmap(scaled);

                    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                    scaled.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos);
                    byte[] b = baos.toByteArray();
                    selectedImageBase64 = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private android.graphics.Bitmap scaleBitmapDown(android.graphics.Bitmap bitmap, int maxDimension) {
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
        return android.graphics.Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private void updateAdminName() {
        Users currentUser = UserSession.getInstance().getUser();
        if (currentUser != null) {
            tvAdminTitle.setText("Quản Trị Viên: " + currentUser.getName());
        } else {
            tvAdminTitle.setText("Quản Trị Viên");
        }
    }

    private void showPopupMenu() {
        PopupMenu popup = new PopupMenu(getContext(), tvAdminTitle);
        popup.getMenuInflater().inflate(R.menu.account_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_edit_profile) {
                openEditDialog();
                return true;
            } else if (itemId == R.id.menu_logout) {
                handleLogout();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void openEditDialog() {
        Users currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa thông tin");

        final EditText input = new EditText(getContext());
        input.setText(currentUser.getName());
        builder.setView(input);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                updateProfile(newName);
            } else {
                Toast.makeText(getContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateProfile(String newName) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(uid)
                .update("name", newName)
                .addOnSuccessListener(aVoid -> {
                    Users currentUser = UserSession.getInstance().getUser();
                    if (currentUser != null) {
                        currentUser.setName(newName);
                    }
                    updateAdminName();
                    Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show());
    }

    private void handleLogout() {
        FirebaseAuth.getInstance().signOut();
        UserSession.getInstance().clear();
        Intent intent = new Intent(getActivity(), LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private static class AdminPagerAdapter extends FragmentStateAdapter {
        public AdminPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new StaffManagementFragment();
            } else if (position == 1) {
                return new RevenueStatisticsFragment();
            }
            return new PaymentHistoryFragment();
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
