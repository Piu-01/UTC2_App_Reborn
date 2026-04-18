package com.utc2.appreborn.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.dormitory.DormitoryActivity;

public class MainActivity extends AppCompatActivity {

    private boolean isOpening = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private Button btnDormitory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDormitory = findViewById(R.id.btnDormitory);

        if (btnDormitory != null) {
            btnDormitory.setOnClickListener(v -> openDormitory());
            btnDormitory.setVisibility(View.VISIBLE);
        }

        // ✅ LIQUID BAR (menu đen)
        ComposeView bottomBarCompose = findViewById(R.id.bottom_bar_compose);

        if (bottomBarCompose != null) {
            LiquidBarKt.setupLiquidBottomBar(
                    bottomBarCompose,
                    id -> {
                        handleNavigation(id);
                        return kotlin.Unit.INSTANCE;
                    }
            );
        }
    }

    private void openDormitory() {
        if (isOpening) return;
        isOpening = true;

        try {
            Intent intent = new Intent(this, DormitoryActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không mở được KTX", Toast.LENGTH_SHORT).show();
        }

        handler.postDelayed(() -> isOpening = false, 500);
    }

    private void handleNavigation(int id) {

        if (id == R.id.nav_home) {

            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();

            if (btnDormitory != null) {
                btnDormitory.setVisibility(View.VISIBLE);
            }

        } else {

            if (btnDormitory != null) {
                btnDormitory.setVisibility(View.GONE);
            }

            if (id == R.id.nav_schedule) {
                Toast.makeText(this, "Lịch học", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_register) {
                Toast.makeText(this, "Đăng ký học phần", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_result) {
                Toast.makeText(this, "Kết quả", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Cá nhân", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}