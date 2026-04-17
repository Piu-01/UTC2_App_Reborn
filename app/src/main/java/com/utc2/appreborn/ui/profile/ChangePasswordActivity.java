package com.utc2.appreborn.ui.profile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.login.FirebaseAuthService;
import com.utc2.appreborn.ui.login.IAuthService;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etNewPass, etConfirmPass;
    private AppCompatButton btnUpdate;
    private ImageButton btnBack;

    // Sử dụng Service chung
    private IAuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();

        btnBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> handlePasswordChange());
    }

    private void initViews() {
        etNewPass = findViewById(R.id.etNewPassword);
        etConfirmPass = findViewById(R.id.etConfirmPassword);
        btnUpdate = findViewById(R.id.btnUpdatePassword);
        btnBack = findViewById(R.id.btnBack);

        // Khởi tạo service
        authService = new FirebaseAuthService();
    }

    private void handlePasswordChange() {
        // 1. Kiểm tra mạng
        if (!isNetworkAvailable()) {
            showToast("Mất mạng rồi bro :V");
            return;
        }

        String newPass = etNewPass.getText().toString().trim();
        String confirmPass = etConfirmPass.getText().toString().trim();

        // 2. Kiểm tra dữ liệu đầu vào
        if (TextUtils.isEmpty(newPass)) {
            etNewPass.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (newPass.length() < 6) {
            showToast("Mật khẩu phải từ 6 ký tự trở lên");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            etConfirmPass.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        // 3. Thực hiện gọi Service
        performChangePassword(newPass);
    }

    private void performChangePassword(String newPassword) {
        btnUpdate.setEnabled(false); // Khóa nút để tránh bấm nhiều lần

        authService.changePassword(newPassword, new IAuthService.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                btnUpdate.setEnabled(true);
                showToast(message);
                finish(); // Thành công thì đóng trang
            }

            @Override
            public void onError(String error) {
                btnUpdate.setEnabled(true);
                // Thông thường lỗi ở đây là do "Requires Recent Login" (Cần login lại)
                showToast("Lỗi: " + error);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}