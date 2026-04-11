package com.utc2.appreborn.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.profile.InfoActivity;
import com.utc2.appreborn.ui.profile.ProfileActivity; // Import trang Profile mới
import com.utc2.appreborn.ui.tuition.TuitionActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnInfo, btnTuition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hiển thị thông báo kiểm tra (có thể xóa sau khi chạy ổn)
        Toast.makeText(this, "Đã vào MainActivity", Toast.LENGTH_SHORT).show();

        // --- CẤU HÌNH LIQUID BAR ---
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

        // --- CÁC NÚT BẤM CŨ TRÊN MÀN HÌNH ---
        btnInfo = findViewById(R.id.btnProfile);
        if (btnInfo != null) {
            btnInfo.setOnClickListener(v -> {
                startActivity(new Intent(this, InfoActivity.class));
            });
        }

        btnTuition = findViewById(R.id.btnTuition);
        if (btnTuition != null) {
            btnTuition.setOnClickListener(v -> {
                startActivity(new Intent(this, TuitionActivity.class));
            });
        }
    }

    /**
     * Xử lý điều hướng khi nhấn vào các icon trên thanh Liquid Bar
     */
    private void handleNavigation(int id) {
        if (id == R.id.nav_home) {
            // Đang ở MainActivity nên chỉ cần thông báo hoặc cuộn lên đầu
            Toast.makeText(this, "Trang chủ", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_schedule) {
            Toast.makeText(this, "Lịch học", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, ScheduleActivity.class);
            // startActivity(intent);

        } else if (id == R.id.nav_register) {
            Toast.makeText(this, "Đăng ký học phần", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_result) {
            Toast.makeText(this, "Kết quả học tập", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_profile) {
            // CHUYỂN HƯỚNG SANG PROFILE
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            // Tránh mở chồng nhiều Activity cùng loại
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }
}