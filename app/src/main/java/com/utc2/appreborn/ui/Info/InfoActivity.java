package com.utc2.appreborn.ui.Info;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.utc2.appreborn.databinding.ActivityInfoBinding;
import com.utc2.appreborn.ui.login.LoginActivity;
import com.utc2.appreborn.ui.profile.SupportActivity;

public class InfoActivity extends AppCompatActivity {

    private ActivityInfoBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();

        // Giả sử đang load dữ liệu từ SQL
        fetchStudentDataFromSQL();
    }

    private void fetchStudentDataFromSQL() {
        // Sau này chỗ này bạn sẽ dùng Retrofit/Volley để gọi API
        // Hiện tại tạo dữ liệu giả để test format
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
        if (info == null) return;

        // Đổ dữ liệu vào các TextView
        binding.tvBirthPlace.setText(info.getBirthPlace());
        binding.tvCCCD.setText(info.getCccd());
        binding.tvAddress.setText(info.getPermanentAddress());
        binding.tvTempAddress.setText(info.getTempAddress());
        binding.tvCurrentAddress.setText(info.getCurrentAddress());

        // Nếu có dùng Glide/Picasso để load ảnh thẻ:
        // Glide.with(this).load(info.getStudentCardUrl()).into(binding.imgStudentCard);
    }

    private void setupClickListeners() {
        // Nút Back: Quay lại màn hình Profile hoặc Main trước đó
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSupport.setOnClickListener(v ->
                startActivity(new Intent(this, SupportActivity.class)));

        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}