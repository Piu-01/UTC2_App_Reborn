package com.utc2.appreborn.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.Info.InfoActivity;
import com.utc2.appreborn.ui.profile.ProfileActivity;
import com.utc2.appreborn.ui.tuition.TuitionActivity;
import com.utc2.appreborn.ui.public_services.PublicServiceActivity; // Import thêm trang Dịch vụ công

public class MainActivity extends AppCompatActivity {

    private Button btnInfo, btnTuition, btnPublicService; // Thêm biến cho nút dịch vụ công

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- CẤU HÌNH LIQUID BAR (GIỮ NGUYÊN) ---
        ComposeView bottomBarCompose = findViewById(R.id.bottom_bar_compose);
        if (bottomBarCompose != null) {
            LiquidBarKt.setupLiquidBottomBar(
                    bottomBarCompose,
                    id -> {
                        handleNavigation(id);
                        return null;
                    }
            );
        }

        // --- CÁC NÚT BẤM TRÊN MÀN HÌNH ---

        // Nút Mở Profile (Info)
        btnInfo = findViewById(R.id.btnProfile);
        if (btnInfo != null) {
            btnInfo.setOnClickListener(v -> {
                startActivity(new Intent(this, InfoActivity.class));
            });
        }

        // Nút Học phí
        btnTuition = findViewById(R.id.btnTuition);
        if (btnTuition != null) {
            btnTuition.setOnClickListener(v -> {
                startActivity(new Intent(this, TuitionActivity.class));
            });
        }

        // --- NÚT DỊCH VỤ CÔNG MỚI ---
        btnPublicService = findViewById(R.id.btnPublicService);
        if (btnPublicService != null) {
            btnPublicService.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, PublicServiceActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi khi mở Dịch vụ công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Xử lý điều hướng khi nhấn vào các icon trên thanh Liquid Bar
     */
    private void handleNavigation(int id) {
        if (id == R.id.nav_home) {
            Toast.makeText(this, "Trang chủ", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_schedule) {
            Toast.makeText(this, "Lịch học", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_register) {
            Toast.makeText(this, "Đăng ký học phần", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_result) {
            Toast.makeText(this, "Kết quả học tập", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }
}