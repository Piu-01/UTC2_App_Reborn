package com.utc2.appreborn.ui.profile;

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
import com.utc2.appreborn.utils.NetworkUtils; // Import Utils

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etNewPass, etConfirmPass;
    private AppCompatButton btnUpdate;
    private ImageButton btnBack;

    private IAuthService authService;
    private NetworkUtils networkUtils; // Thêm biến NetworkUtils

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();
        setupNetworkMonitoring(); // Khởi tạo lắng nghe mạng

        btnBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> handlePasswordChange());
    }

    private void initViews() {
        etNewPass = findViewById(R.id.etNewPassword);
        etConfirmPass = findViewById(R.id.etConfirmPassword);
        btnUpdate = findViewById(R.id.btnUpdatePassword);
        btnBack = findViewById(R.id.btnBack);

        authService = new FirebaseAuthService();
    }

    // Thiết lập lắng nghe mạng liên tục
    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                btnUpdate.setEnabled(true);
                btnUpdate.setAlpha(1.0f);
            }

            @Override
            public void onNetworkLost() {
                showToast("Mất kết nối mạng!");
                btnUpdate.setEnabled(false);
                btnUpdate.setAlpha(0.5f);
            }
        });
        networkUtils.register();
    }

    private void handlePasswordChange() {
        // Sử dụng static method từ Utils để check tức thời
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showToast("Mất mạng rồi bro :V");
            return;
        }

        String newPass = etNewPass.getText().toString().trim();
        String confirmPass = etConfirmPass.getText().toString().trim();

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

        performChangePassword(newPass);
    }

    private void performChangePassword(String newPassword) {
        btnUpdate.setEnabled(false);

        authService.changePassword(newPassword, new IAuthService.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                btnUpdate.setEnabled(true);
                showToast(message);
                finish();
            }

            @Override
            public void onError(String error) {
                btnUpdate.setEnabled(true);
                showToast("Lỗi: " + error);
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký để tránh leak memory
        if (networkUtils != null) {
            networkUtils.unregister();
        }
    }
}