package com.utc2.appreborn.ui.Info;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.utc2.appreborn.databinding.ActivityInfoBinding;
import com.utc2.appreborn.ui.login.LoginActivity;
import com.utc2.appreborn.ui.profile.SupportActivity;
import com.utc2.appreborn.utils.NetworkUtils; // Import Utils của bạn

public class InfoActivity extends AppCompatActivity {

    private ActivityInfoBinding binding;
    private FirebaseAuth mAuth;
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Kiểm tra đăng nhập trước khi inflate layout để tiết kiệm tài nguyên
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            navigateToLogin();
            return;
        }

        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            setupNetworkMonitoring();
            setupClickListeners();
            fetchStudentDataFromSQL(); // Load dữ liệu
        } catch (Exception e) {
            Log.e("InfoActivity", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                Log.d("Network", "Đã có mạng - Sẵn sàng cập nhật thông tin sinh viên");
                // Nếu cần, bạn có thể gọi lại fetchStudentDataFromSQL() ở đây để refresh dữ liệu
            }

            @Override
            public void onNetworkLost() {
                Toast.makeText(InfoActivity.this,
                        "Bạn đang ngoại tuyến. Thông tin hiển thị có thể là dữ liệu cũ.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    private void fetchStudentDataFromSQL() {
        // Giả lập dữ liệu (Mốt bạn lụm từ API qua Retrofit hoặc Firebase Database)
        StudentInfo data = new StudentInfo(
                "Xã gió đó, Tỉnh Đồng Thuận",
                "083385896945785884",
                "Đồng Thuận, Việt Nam",
                "Quận 9, TP. Hồ Chí Minh",
                "Ký túc xá khu B, ĐHQG",
                "https://link-anh-the.com/user.jpg"
        );

        updateUI(data);
    }

    private void updateUI(StudentInfo info) {
        if (info == null || binding == null) return;

        // Đổ dữ liệu vào các TextView qua Binding (Rất an toàn, không lo Null ID)
        binding.tvBirthPlace.setText(info.getBirthPlace());
        binding.tvCCCD.setText(info.getCccd());
        binding.tvAddress.setText(info.getPermanentAddress());
        binding.tvTempAddress.setText(info.getTempAddress());
        binding.tvCurrentAddress.setText(info.getCurrentAddress());
    }

    private void setupClickListeners() {
        // Sử dụng Expression Lambda để xóa cảnh báo vàng
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSupport.setOnClickListener(v ->
                startActivity(new Intent(this, SupportActivity.class)));

        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            navigateToLogin();
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký lắng nghe mạng
        if (networkUtils != null) {
            networkUtils.unregister();
        }
        // Giải phóng binding để tránh rò rỉ bộ nhớ
        binding = null;
    }
}