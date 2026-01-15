package com.example.billards.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.billards.R;
import com.example.billards.Models.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.billards.Models.Users;


public class SignUpScreen extends AppCompatActivity {

    EditText edtname, edtemail,edtpass;

    Button btnsignup;
    private FirebaseAuth mAuth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String email="";
    private String pass="";

    private String name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtname=findViewById(R.id.edtname);
        edtemail=findViewById(R.id.edtemail);
        edtpass=findViewById(R.id.edtpass);
        btnsignup=findViewById(R.id.btnsignup);
        mAuth = FirebaseAuth.getInstance();
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(edtname,edtemail,edtpass);
            }
        });

    }

    private void saveUserToFirestore(String uid, String name, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Khởi tạo đối tượng User với role mặc định là staff
        Users newUser = new Users(uid,name, email, "staff");

        // Lưu vào collection "users" với ID là uid của Auth
        db.collection("users").document(uid)
                .set(newUser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignUpScreen.this, "Đã lưu nhân viên vào Database", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", e.getMessage());
                });
    }
    private void createUserWithEmailAndPassword(String name,String email, String pass){
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserToFirestore(user.getUid(),name,email);
//                            Intent intent=new Intent(SignUpScreen.this,MainActivity.class);
//                            startActivity(intent);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void createAccount(EditText edtname,EditText edtuser, EditText edtpass){
        name=edtname.getText().toString().trim();
        email=edtuser.getText().toString().trim();
        pass=edtpass.getText().toString().trim();
        if(email != null || name!=null){
            createUserWithEmailAndPassword(name,email,pass);
        }
    }





}