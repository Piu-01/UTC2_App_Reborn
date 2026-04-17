package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView; // Import cho ImageView
import android.widget.LinearLayout;
import android.widget.Toast; // Import cho Toast
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.compose.ui.platform.ComposeView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.Info.InfoActivity;
import com.utc2.appreborn.ui.profile.TrainingProgram.TrainingProgramActivity;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout layoutSubjectList, layoutGraduationReq;
    private AppCompatButton btnInfo, btnChangePassword;
    private ImageView btnNotification; // Khai báo biến cho chuông
    private ComposeView bottomBarCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupBottomBar();
        setClickListeners();
    }

    private void initViews() {
        layoutSubjectList = findViewById(R.id.layoutSubjectList);
        layoutGraduationReq = findViewById(R.id.layoutGraduationReq);
        bottomBarCompose = findViewById(R.id.bottom_bar_compose);
        btnInfo = findViewById(R.id.btnProfileInfo);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Ánh xạ nút chuông
        btnNotification = findViewById(R.id.btnNotification);
    }

    private void setupBottomBar() {
        LiquidBarKt.setupLiquidBottomBar(bottomBarCompose, id -> {
            if (id == R.id.nav_home) {
                finish();
            }
            return null;
        });
    }

    private void setClickListeners() {
        // 1. Nút Thông tin cá nhân
        btnInfo.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, InfoActivity.class));
        });

        // 2. Nút Đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
        });

        // 3. Sự kiện cho nút Chuông thông báo
        btnNotification.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Không có thông báo mới", Toast.LENGTH_SHORT).show();
        });

        // 4. Danh sách học phần
        layoutSubjectList.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, TrainingProgramActivity.class));
        });

        // 5. Yêu cầu tốt nghiệp
        layoutGraduationReq.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, GraduationRequirementsActivity.class));
        });
    }
}