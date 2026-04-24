package com.utc2.appreborn.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.courseregistration.CourseRegistrationActivity;

public class MainActivity extends AppCompatActivity {

    private boolean isOpening = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ComposeView bottomBarCompose = findViewById(R.id.bottom_bar_compose);

        LiquidBarKt.setupLiquidBottomBar(
                bottomBarCompose,
                id -> {
                    handleNavigation(id);
                    return null;
                }
        );
    }

    private void handleNavigation(int id) {
        if (id == R.id.nav_home) {
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_schedule) {
            Toast.makeText(this, "Lịch học", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_register) {
            // ✅ Mở màn hình Đăng ký học phần
            openCourseRegistration();

        } else if (id == R.id.nav_result) {
            Toast.makeText(this, "Kết quả học tập", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_profile) {
            Toast.makeText(this, "Cá nhân", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCourseRegistration() {
        if (isOpening) return;
        isOpening = true;
        try {
            Intent intent = new Intent(this, CourseRegistrationActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không mở được Đăng ký học phần", Toast.LENGTH_SHORT).show();
        }
        handler.postDelayed(() -> isOpening = false, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}