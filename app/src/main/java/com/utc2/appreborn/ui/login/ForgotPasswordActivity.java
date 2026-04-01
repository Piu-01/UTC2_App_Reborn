package com.utc2.appreborn.ui.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.utc2.appreborn.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edtEmail;
    Button btnReset;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.edtEmail);
        btnReset = findViewById(R.id.btnReset);

        auth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(v -> sendResetEmail());
    }

    private void sendResetEmail() {

        String email = edtEmail.getText().toString().trim();

        if(email.isEmpty()){
            edtEmail.setError(getString(R.string.email_required));
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            getString(R.string.reset_email_sent),
                            Toast.LENGTH_LONG).show();

                    finish(); // quay lại login
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }
}