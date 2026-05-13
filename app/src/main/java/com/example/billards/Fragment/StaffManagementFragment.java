package com.example.billards.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StaffManagementFragment extends Fragment {

    private RecyclerView rvStaff;
    private FloatingActionButton fabAddStaff;
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

        staffList = new ArrayList<>();
        adapter = new StaffAdapter(staffList, this::showDeleteConfirmDialog);
        rvStaff.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStaff.setAdapter(adapter);

        loadStaffList();

        fabAddStaff.setOnClickListener(v -> showAddStaffDialog());
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

    private void createStaffAccount(String name, String email, String password) {
        // Lưu ý: Việc tạo user mới sẽ tự động đăng nhập user đó. 
        // Trong thực tế Admin nên dùng Firebase Admin SDK hoặc một Cloud Function.
        // Ở mức độ client, sau khi tạo xong ta cần sign out và sign in lại admin hoặc chỉ lưu vào Firestore.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    Users newUser = new Users(uid, name, email, "staff");
                    db.collection("users").document(uid).set(newUser)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Đã thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
}
