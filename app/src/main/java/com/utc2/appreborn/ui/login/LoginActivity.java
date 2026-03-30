package com.utc2.appreborn.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.utc2.appreborn.ui.main.MainActivity;
import com.utc2.appreborn.R;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn, skipBtn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        skipBtn = findViewById(R.id.skipBtn);

        auth = FirebaseAuth.getInstance();

        // LOGIN BUTTON
        loginBtn.setOnClickListener(v -> {

            String userEmail = email.getText().toString();
            String userPass = password.getText().toString();
            //check xem có để trống email ko
            if(userEmail.isEmpty()){
                email.setError("Nhập email đi bro :V");
                email.requestFocus();
                return;
            }
            //check xem có để trống mk ko
            if(userPass.isEmpty()){
                password.setError("Nhập password đi bro :V");
                password.requestFocus();
                return;
            }
            auth.signInWithEmailAndPassword(userEmail, userPass)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // ✅ SKIP LOGIN
        skipBtn.setOnClickListener(v -> {

            auth.signInWithEmailAndPassword(
                    "test123@gmail.com",
                    "123456"
            ).addOnCompleteListener(task -> {

                if(task.isSuccessful()){
                    startActivity(new Intent(this, MainActivity.class));
                    Toast.makeText(this, "Skip Success", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(this,"Skip Failed",Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}