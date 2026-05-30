package com.example.billards.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billards.Models.StaffAdapter;
import com.example.billards.Models.Users;
import com.example.billards.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StaffManagementFragment extends Fragment {

    private static final String TAG = "StaffManagement";

    private RecyclerView rvStaff;
    private FloatingActionButton fabAddStaff;
    private MaterialButton btnAddStaff;
    private StaffAdapter adapter;
    private List<Users> staffList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        rvStaff = view.findViewById(R.id.rvStaff);
        fabAddStaff = view.findViewById(R.id.fabAddStaff);
        btnAddStaff = view.findViewById(R.id.btnAddStaff);

        staffList = new ArrayList<>();
        adapter = new StaffAdapter(staffList, this::showDeleteConfirmDialog, this::showResetPasswordConfirmDialog);
        rvStaff.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStaff.setAdapter(adapter);

        loadStaffList();

        View.OnClickListener addStaffListener = v -> showAddStaffDialog();
        if (btnAddStaff != null) btnAddStaff.setOnClickListener(addStaffListener);
        if (fabAddStaff != null) fabAddStaff.setOnClickListener(addStaffListener);
    }

    private void loadStaffList() {
        db.collection("users")
                .whereEqualTo("role", "staff")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    staffList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Users user = doc.toObject(Users.class);
                            user.setUid(doc.getId());
                            staffList.add(user);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void showAddStaffDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_staff, null);
        EditText etName = view.findViewById(R.id.etStaffName);
        EditText etEmail = view.findViewById(R.id.etStaffEmail);
        EditText etPassword = view.findViewById(R.id.etStaffPassword);

        new AlertDialog.Builder(getContext())
                .setTitle("Thêm nhân viên mới")
                .setView(view)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                        Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    createStaffAccount(name, email, password);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Tạo tài khoản nhân viên bằng FirebaseAuth instance phụ
     * để tránh đăng xuất admin khỏi phiên hiện tại.
     * createUserWithEmailAndPassword() tự động đăng nhập user mới tạo,
     * nên cần dùng instance riêng biệt.
     */
    private void createStaffAccount(String name, String email, String password) {
        // Lưu lại thông tin admin hiện tại
        FirebaseUser adminUser = mAuth.getCurrentUser();

        // Tạo FirebaseApp phụ để tạo tài khoản mà không ảnh hưởng đến phiên admin
        FirebaseApp secondaryApp;
        try {
            secondaryApp = FirebaseApp.getInstance("staffCreator");
        } catch (IllegalStateException e) {
            FirebaseOptions options = FirebaseApp.getInstance().getOptions();
            secondaryApp = FirebaseApp.initializeApp(requireContext(), options, "staffCreator");
        }

        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp);

        secondaryAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    Users newUser = new Users(uid, name, email, "staff");

                    // Đăng xuất khỏi instance phụ ngay lập tức
                    secondaryAuth.signOut();

                    db.collection("users").document(uid).set(newUser)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Đã thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Staff account created: " + email + " (UID: " + uid + ")");
                            })
                            .addOnFailureListener(err -> {
                                Log.e(TAG, "Failed to save staff to Firestore", err);
                                Toast.makeText(getContext(), "Lỗi lưu thông tin: " + err.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to create staff account", e);
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteConfirmDialog(Users user) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa nhân viên " + user.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    db.collection("users").document(user.getUid()).delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã xóa nhân viên", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showResetPasswordConfirmDialog(Users user) {
        new AlertDialog.Builder(getContext())
                .setTitle("Cấp lại mật khẩu")
                .setMessage("Bạn có chắc chắn muốn gửi email khôi phục mật khẩu đến nhân viên " + user.getName() + " (" + user.getEmail() + ") không?")
                .setPositiveButton("Gửi Email", (dialog, which) -> {
                    sendResetPasswordEmail(user.getEmail());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void sendResetPasswordEmail(String email) {
        // Validate email trước khi gửi
        if (TextUtils.isEmpty(email)) {
            Log.e(TAG, "sendResetPasswordEmail: email is null or empty");
            Toast.makeText(getContext(), "Lỗi: Email nhân viên không tồn tại trong hệ thống!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Sending password reset email to: " + email);

        // Sử dụng FirebaseAuth default instance (không cần đăng nhập để gửi reset email)
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Password reset email sent successfully to: " + email);
                    Toast.makeText(getContext(), "Đã gửi email khôi phục mật khẩu đến " + email + "!", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to send password reset email to: " + email, e);
                    Toast.makeText(getContext(), "Lỗi gửi email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}

