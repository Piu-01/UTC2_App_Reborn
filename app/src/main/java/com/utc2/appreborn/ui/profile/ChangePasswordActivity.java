package com.utc2.appreborn.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utc2.appreborn.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etNewPass, etConfirmPass;
    private AppCompatButton btnUpdate;
    private ImageButton btnBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // 1. Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. Ánh xạ View
        initViews();

        // 3. Sự kiện nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // 4. Sự kiện nút cập nhật
        btnUpdate.setOnClickListener(v -> handlePasswordChange());
    }

    private void initViews() {
        etNewPass = findViewById(R.id.etNewPassword);
        etConfirmPass = findViewById(R.id.etConfirmPassword);
        btnUpdate = findViewById(R.id.btnUpdatePassword);
        btnBack = findViewById(R.id.btnBack);
    }

    private void handlePasswordChange() {
        String newPass = etNewPass.getText().toString().trim();
        String confirmPass = etConfirmPass.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (TextUtils.isEmpty(newPass)) {
            etNewPass.setError(getString(R.string.hint_new_password));
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            etConfirmPass.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        // Thực hiện đổi mật khẩu trên Firebase
        updateFirebasePassword(newPass);
    }

    private void updateFirebasePassword(String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Hiển thị loading nếu cần (ProgressBar)
            btnUpdate.setEnabled(false);

            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        btnUpdate.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Cập nhật mật khẩu thành công!", Toast.LENGTH_LONG).show();
                            finish(); // Đóng trang sau khi thành công
                        } else {
                            // Lỗi thường gặp: Người dùng cần đăng nhập lại để thực hiện thao tác bảo mật
                            String errorMsg = task.getException() != null ?
                                    task.getException().getMessage() : "Lỗi không xác định";
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
        }
    }
}