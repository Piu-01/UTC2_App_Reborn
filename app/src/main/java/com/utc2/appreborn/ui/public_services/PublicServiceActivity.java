package com.utc2.appreborn.ui.public_services;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.public_services.CardReissueService.CardReissueActivity;
import com.utc2.appreborn.ui.public_services.CardReissueService.CardReissueService;
import com.utc2.appreborn.ui.public_services.LoanSupportService.LoanSupportActivity;
import com.utc2.appreborn.ui.public_services.LoanSupportService.LoanSupportService;
import com.utc2.appreborn.ui.public_services.StudentConfirmationService.StudentConfirmationActivity;
import com.utc2.appreborn.ui.public_services.StudentConfirmationService.StudentConfirmationService;
import com.utc2.appreborn.ui.public_services.TranscriptService.TranscriptRegistrationActivity;
import com.utc2.appreborn.ui.public_services.TranscriptService.TranscriptService;
import com.utc2.appreborn.utils.NetworkUtils; // Import Utils

import java.util.ArrayList;
import java.util.List;

public class PublicServiceActivity extends AppCompatActivity {

    private ScrollView layoutDichVuMenu;
    private RecyclerView rvKetQua;
    private TextView btnDichVu, btnKetQua, txtSectionTitle;
    private PublicServiceAdapter adapter;
    private List<BaseService> historyList = new ArrayList<>();

    // Thêm biến quản lý mạng
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_service);

        try {
            initViews();
            setupRecyclerView();
            setupNetworkMonitoring(); // Khởi tạo lắng nghe mạng
            setupEvents();

            showTabDichVu();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                // Nếu đang ở Tab Kết quả thì tự động load lại dữ liệu mới nhất
                if (rvKetQua.getVisibility() == View.VISIBLE) {
                    loadHistoryData();
                }
            }

            @Override
            public void onNetworkLost() {
                Toast.makeText(PublicServiceActivity.this,
                        "Bạn đang ngoại tuyến. Một số tính năng đăng ký sẽ bị tạm dừng.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    private void initViews() {
        layoutDichVuMenu = findViewById(R.id.layoutDichVuMenu);
        rvKetQua = findViewById(R.id.rvKetQua);
        btnDichVu = findViewById(R.id.btnDichVu);
        btnKetQua = findViewById(R.id.btnKetQua);
        txtSectionTitle = findViewById(R.id.sectionTitle);
    }

    private void setupRecyclerView() {
        rvKetQua.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupEvents() {
        btnDichVu.setOnClickListener(v -> showTabDichVu());
        btnKetQua.setOnClickListener(v -> showTabKetQua());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Bọc sự kiện click bằng check mạng để tránh user vào đăng ký khi ko có mạng
        findViewById(R.id.btnCardReissueMenu).setOnClickListener(v -> checkNetAndNavigate(CardReissueActivity.class));
        findViewById(R.id.btnLoanSupportMenu).setOnClickListener(v -> checkNetAndNavigate(LoanSupportActivity.class));
        findViewById(R.id.btnTranscriptMenu).setOnClickListener(v -> checkNetAndNavigate(TranscriptRegistrationActivity.class));
        findViewById(R.id.btnConfirmationMenu).setOnClickListener(v -> checkNetAndNavigate(StudentConfirmationActivity.class));
    }

    // Hàm tiện ích: Check mạng xong mới cho chuyển trang
    private void checkNetAndNavigate(Class<?> destination) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, destination));
        } else {
            Toast.makeText(this, "Cần có mạng để thực hiện thủ tục này!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTabDichVu() {
        updateToggleUI(true);
        layoutDichVuMenu.setVisibility(View.VISIBLE);
        rvKetQua.setVisibility(View.GONE);

        if (txtSectionTitle != null) {
            txtSectionTitle.setText(R.string.section_public_service);
            txtSectionTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hand_platter, 0, 0, 0);
        }
    }

    private void showTabKetQua() {
        updateToggleUI(false);
        layoutDichVuMenu.setVisibility(View.GONE);
        rvKetQua.setVisibility(View.VISIBLE);

        if (txtSectionTitle != null) {
            txtSectionTitle.setText(R.string.tab_results);
            txtSectionTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_scroll_text, 0, 0, 0);
        }

        loadHistoryData();
    }

    private void loadHistoryData() {
        // Sau này thay đống dữ liệu mẫu này bằng việc gọi Web API
        // if (!NetworkUtils.isNetworkAvailable(this)) { ... hiện dữ liệu cũ từ SQLite/Room ... }

        historyList.clear();
        long now = System.currentTimeMillis();

        historyList.add(new CardReissueService(
                getString(R.string.reissue_card_title),
                "Lý do: Thẻ bị hỏng chip", now, 1, "CARD_REISSUE",
                getString(R.string.default_name), getString(R.string.default_mssv), getString(R.string.default_class)));

        historyList.add(new TranscriptService(
                getString(R.string.transcript_registration_title),
                "Số lượng: 03 bản", now - 3600000, 0, "TRANSCRIPT_REG",
                getString(R.string.default_name), getString(R.string.default_mssv), getString(R.string.default_class),
                "2023 - 2024", "Học kỳ 2", "03"));

        historyList.add(new StudentConfirmationService(
                getString(R.string.student_confirmation_title),
                "Lý do: Làm hồ sơ thực tập", now - 86400000, 1, "STUDENT_CONFIRM",
                getString(R.string.default_name), getString(R.string.default_mssv), getString(R.string.default_class)));

        historyList.add(new LoanSupportService(
                getString(R.string.loan_support_title),
                "Số tiền: 10.000.000đ", now - 172800000, 0, "LOAN_SUPPORT",
                "10000000", "Học kỳ 1", getString(R.string.default_phone)));

        adapter = new PublicServiceAdapter(historyList, true);
        rvKetQua.setAdapter(adapter);
    }

    private void updateToggleUI(boolean isDichVuSelected) {
        if (isDichVuSelected) {
            btnDichVu.setBackgroundResource(R.drawable.bg_toggle_selected);
            btnDichVu.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnDichVu.setTypeface(null, android.graphics.Typeface.BOLD);

            btnKetQua.setBackgroundResource(R.drawable.bg_toggle_container);
            btnKetQua.setTextColor(ContextCompat.getColor(this, R.color.black));
            btnKetQua.setTypeface(null, android.graphics.Typeface.NORMAL);
        } else {
            btnKetQua.setBackgroundResource(R.drawable.bg_toggle_selected);
            btnKetQua.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnKetQua.setTypeface(null, android.graphics.Typeface.BOLD);

            btnDichVu.setBackgroundResource(R.drawable.bg_toggle_container);
            btnDichVu.setTextColor(ContextCompat.getColor(this, R.color.black));
            btnDichVu.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Luôn hủy đăng ký để tránh memory leak
        if (networkUtils != null) {
            networkUtils.unregister();
        }
    }
}