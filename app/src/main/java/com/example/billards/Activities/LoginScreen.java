package com.example.billards.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.billards.Models.UserSession;
import com.example.billards.Models.Users;
import com.example.billards.R;
import com.example.billards.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginScreen extends AppCompatActivity {
    EditText edtuser;
    EditText edtpass;
    Button btnlogin;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        
        mAuth = FirebaseAuth.getInstance();

        // === AUTO-LOGIN: Kiểm tra session đã lưu ===
        if (SessionManager.isLoggedIn(this)) {
            Users cachedUser = SessionManager.loadSession(this);
            if (cachedUser != null) {
                UserSession.getInstance().setUser(cachedUser);
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return;
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtuser = findViewById(R.id.edtemail);
        edtpass = findViewById(R.id.edtpass);
        btnlogin = findViewById(R.id.btnlogin);
        progressBar = findViewById(R.id.progressBar);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtuser.getText().toString().trim();
                String password = edtpass.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty()) {
                    signIn(email, password);
                } else {
                    Toast.makeText(LoginScreen.this, "Vui lòng nhập đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        btnlogin.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Log.d(TAG, "Auth successful. UID: " + user.getUid());
                                fetchUserDataAndNavigate(user.getUid(), user.getEmail());
                            }
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginScreen.this, "Đăng nhập thất bại: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            btnlogin.setEnabled(true);
                        }
                    }
                });
    }

    private void fetchUserDataAndNavigate(String uid, String email) {
        // Lớp 1: Tìm theo Document ID (UID)
        db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "User found by UID");
                        handleUserNavigation(document, uid);
                    } else {
                        // Lớp 2: Dự phòng - Tìm theo trường email nếu UID không khớp ID Document
                        Log.w(TAG, "UID not found as Document ID, trying fallback search by email field");
                        searchUserByEmail(email, uid);
                    }
                } else {
                    Log.e(TAG, "Firestore error: ", task.getException());
                    Toast.makeText(LoginScreen.this, "Lỗi kết nối cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchUserByEmail(String email, String uid) {
        db.collection("users").whereEqualTo("email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            Log.d(TAG, "User found by email field fallback");
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            handleUserNavigation(document, uid);
                        } else {
                            Log.e(TAG, "User not found in Firestore by UID or Email");
                            Toast.makeText(LoginScreen.this, "Không tìm thấy thông tin người dùng trong Database!", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }
                    }
                });
    }

    private void handleUserNavigation(DocumentSnapshot document, String uid) {
        Users userModel = document.toObject(Users.class);
        if (userModel != null) {
            // Đảm bảo UID của object là UID từ Auth
            if (userModel.getUid() == null || userModel.getUid().isEmpty()) {
                userModel.setUid(uid);
            }
            
            UserSession.getInstance().setUser(userModel);
            // Lưu session vào cache để không cần đăng nhập lại
            SessionManager.saveSession(this, userModel);
            
            Intent intent = new Intent(LoginScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(LoginScreen.this, "Chào mừng: " + userModel.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Mapping Firestore data to Users.class failed");
            Toast.makeText(LoginScreen.this, "Lỗi định dạng dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
        }
    }
}
