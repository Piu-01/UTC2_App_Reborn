package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.compose.ui.platform.ComposeView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.Info.InfoActivity;
import com.utc2.appreborn.ui.profile.TrainingProgram.TrainingProgramActivity;
import com.utc2.appreborn.utils.NetworkUtils; // Import Utils

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout layoutSubjectList, layoutGraduationReq;
    private AppCompatButton btnInfo, btnChangePassword;
    private ImageView btnNotification;
    private ComposeView bottomBarCompose;

    // Quản lý kết nối mạng
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupBottomBar();
        setupNetworkMonitoring(); // Thiết lập theo dõi mạng
        setClickListeners();
    }

    private void initViews() {
        layoutSubjectList = findViewById(R.id.layoutSubjectList);
        layoutGraduationReq = findViewById(R.id.layoutGraduationReq);
        bottomBarCompose = findViewById(R.id.bottom_bar_compose);
        btnInfo = findViewById(R.id.btnProfileInfo);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnNotification = findViewById(R.id.btnNotification);
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                // Khi có mạng: Cho phép click các chức năng nhạy cảm
                btnChangePassword.setEnabled(true);
                btnChangePassword.setAlpha(1.0f);
            }

            @Override
            public void onNetworkLost() {
                // Khi mất mạng: Thông báo và làm mờ nút đổi mật khẩu
                showToast("Bạn đang ngoại tuyến. Một số tính năng sẽ bị hạn chế.");
                btnChangePassword.setEnabled(false);
                btnChangePassword.setAlpha(0.5f);
            }
        });
        networkUtils.register();
    }

    private void setupBottomBar() {
        LiquidBarKt.setupLiquidBottomBar(bottomBarCompose, id -> {
            if (id == R.id.nav_home) {
                finish();
            }
            return kotlin.Unit.INSTANCE;
        });
    }

    private void setClickListeners() {
        btnInfo.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, InfoActivity.class));
        });

        btnChangePassword.setOnClickListener(v -> {
            // Check tức thời trước khi chuyển màn hình
            if (NetworkUtils.isNetworkAvailable(this)) {
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            } else {
                showToast("Vui lòng kết nối mạng để thực hiện tính năng này!");
            }
        });

        btnNotification.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                showToast("Không có thông báo mới");
            } else {
                showToast("Không thể tải thông báo lúc này");
            }
        });

        layoutSubjectList.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, TrainingProgramActivity.class));
        });

        layoutGraduationReq.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, GraduationRequirementsActivity.class));
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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