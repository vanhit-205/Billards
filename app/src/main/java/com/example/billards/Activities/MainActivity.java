package com.example.billards.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.billards.Fragment.AdminFragment;
import com.example.billards.Fragment.StaffFragment;
import com.example.billards.Models.Payment;
import com.example.billards.Models.UserSession;
import com.example.billards.Models.Users;
import com.example.billards.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {

            // Lấy thông tin bàn từ Intent (đã được truyền từ TableAdapter)
            String tableId = data.getStringExtra("TABLE_ID");
            int tableNumber = data.getIntExtra("TABLE_NUMBER", -1);
            long totalAmount = data.getLongExtra("TOTAL_AMOUNT", 0);
            long diff = data.getLongExtra("DIFF", 0);

            if (tableId != null && tableNumber != -1) {
                // Gọi logic reset bàn giống processPayment trong TableAdapter
                DocumentReference tableRef = db.collection("table").document(tableId);
                DocumentReference paymentRef = db.collection("payments").document();
                Payment payment = new Payment(paymentRef.getId(), tableNumber,
                        System.currentTimeMillis(), diff, totalAmount, "vnpay", "completed");

                paymentRef.set(payment).addOnSuccessListener(aVoid -> {
                    tableRef.update("isPlaying", false, "startTime", 0)
                            .addOnSuccessListener(unused -> {
                                // Xóa các order cũ của bàn này
                                db.collection("orders").whereEqualTo("tableID", tableNumber).get()
                                        .addOnSuccessListener(snapshots -> {
                                            for (DocumentSnapshot doc : snapshots) {
                                                doc.getReference().delete();
                                            }
                                        });
                                Toast.makeText(this, "VNPAY: Thanh toán thành công!", Toast.LENGTH_LONG).show();
                            });
                });
            } else {
                Toast.makeText(this, "VNPAY: Thanh toán thành công!", Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == 999 && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Thanh toán đã bị hủy hoặc thất bại", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Users currentUser = UserSession.getInstance().getUser();

        // Kiểm tra nếu session không tồn tại (tránh crash ứng dụng)
        if (currentUser == null) {
            Toast.makeText(this, "Phiên làm việc hết hạn, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginScreen.class);
            startActivity(intent);
            finish();
            return;
        }

        // Phân quyền dựa trên Role
        if ("staff".equals(currentUser.getRole())) {
            loadFragment(new StaffFragment());
        } else if ("admin".equals(currentUser.getRole())) {
            loadFragment(new AdminFragment());
        } else {
            Toast.makeText(this, "Tài khoản không có quyền truy cập!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}
