package com.utc2.appreborn.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn, skipBtn;
    TextView txtForgot, txtTerms;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // INIT VIEW
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        skipBtn = findViewById(R.id.skipBtn);
        txtForgot = findViewById(R.id.txtForgot);
        txtTerms = findViewById(R.id.txtTerms);

        auth = FirebaseAuth.getInstance();

        // ================= FORGOT PASSWORD =================
        txtForgot.setOnClickListener(v ->
                startActivity(new Intent(
                        LoginActivity.this,
                        ForgotPasswordActivity.class
                ))
        );

        // ================= TERMS + PRIVACY LINK =================
        String text =
                "By continuing you agree to our Terms of Service and Privacy Policy";

        SpannableString spannable = new SpannableString(text);

        // TERMS CLICK
        ClickableSpan termsClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(
                        LoginActivity.this,
                        TermsActivity.class));
            }
        };

        // PRIVACY CLICK
        ClickableSpan privacyClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(
                        LoginActivity.this,
                        PrivacyPolicyActivity.class));
            }
        };

        // APPLY SPAN
        spannable.setSpan(
                termsClick,
                text.indexOf("Terms of Service"),
                text.indexOf("Terms of Service") + "Terms of Service".length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                privacyClick,
                text.indexOf("Privacy Policy"),
                text.indexOf("Privacy Policy") + "Privacy Policy".length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        txtTerms.setText(spannable);
        txtTerms.setMovementMethod(LinkMovementMethod.getInstance());
        txtTerms.setHighlightColor(Color.TRANSPARENT);

        // ================= LOGIN =================
        loginBtn.setOnClickListener(v -> {

            String userEmail = email.getText().toString().trim();
            String userPass = password.getText().toString().trim();

            if (userEmail.isEmpty()) {
                email.setError("Nhập email đi bro :V");
                email.requestFocus();
                return;
            }

            if (userPass.isEmpty()) {
                password.setError("Nhập password đi bro :V");
                password.requestFocus();
                return;
            }

            auth.signInWithEmailAndPassword(userEmail, userPass)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            Toast.makeText(this,
                                    "Login Success",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(
                                    new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this,
                                    "Sai email hoặc mật khẩu!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // ================= SKIP LOGIN =================
        skipBtn.setOnClickListener(v -> {

            auth.signInWithEmailAndPassword(
                    "test123@gmail.com",
                    "123456"
            ).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    Toast.makeText(this,
                            "Skip Success",
                            Toast.LENGTH_SHORT).show();

                    startActivity(
                            new Intent(this, MainActivity.class));
                    finish();

                } else {
                    Toast.makeText(this,
                            "Skip Failed",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}