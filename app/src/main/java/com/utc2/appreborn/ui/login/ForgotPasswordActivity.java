package com.utc2.appreborn.ui.login;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnReset;
    private ImageButton btnBack;

    // Vẫn dùng Interface để mốt đổi Backend cho dễ
    private IAuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút gửi yêu cầu reset
        btnReset.setOnClickListener(v -> validateAndReset());
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        btnReset = findViewById(R.id.btnReset);
        btnBack = findViewById(R.id.btnBack);

        // Khởi tạo service (Firebase)
        authService = new FirebaseAuthService();
    }

    private void validateAndReset() {
        // Kiểm tra mạng trước khi gửi
        if (!isNetworkAvailable()) {
            showToast("Mất mạng rồi bro ơi :V");
            return;
        }

        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Nhập email nhận pass đi bro :V");
            edtEmail.requestFocus();
            return;
        }

        performReset(email);
    }

    private void performReset(String email) {
        // Vô hiệu hóa nút để tránh bấm nhiều lần khi đang đợi mail gửi đi
        btnReset.setEnabled(false);

        authService.resetPassword(email, new IAuthService.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                showToast(message);
                finish(); // Thành công thì đóng trang quay về Login
            }

            @Override
            public void onError(String error) {
                btnReset.setEnabled(true); // Lỗi thì bật lại nút cho bấm lại
                showToast(error);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}