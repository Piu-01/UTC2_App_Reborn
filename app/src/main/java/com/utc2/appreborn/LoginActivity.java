package com.utc2.appreborn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);

        auth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(v -> {

            String userEmail = email.getText().toString();
            String userPass = password.getText().toString();

            auth.signInWithEmailAndPassword(userEmail, userPass)
                    .addOnCompleteListener(task -> {

                        if(task.isSuccessful()){
                            Toast.makeText(this,"Login Success",Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(this, MainActivity.class));
                            finish();

                        }else{
                            Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show();
                        }

                    });

        });
    }
}