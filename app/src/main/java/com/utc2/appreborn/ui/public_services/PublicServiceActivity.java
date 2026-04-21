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

import java.util.ArrayList;
import java.util.List;

public class PublicServiceActivity extends AppCompatActivity {

    private ScrollView layoutDichVuMenu;
    private RecyclerView rvKetQua;
    private TextView btnDichVu, btnKetQua, txtSectionTitle;
    private PublicServiceAdapter adapter;
    private List<BaseService> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_service);

        try {
            initViews();
            setupRecyclerView();
            setupEvents();

            // Mặc định ban đầu hiện tab Dịch vụ
            showTabDichVu();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        // Chuyển Tab
        btnDichVu.setOnClickListener(v -> showTabDichVu());
        btnKetQua.setOnClickListener(v -> showTabKetQua());

        // Nút Quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // --- Bắt sự kiện click cho các Card tĩnh (Tab Dịch vụ) ---

        // 1. Nút Cấp lại thẻ
        findViewById(R.id.btnCardReissueMenu).setOnClickListener(v -> {
            startActivity(new Intent(this, CardReissueActivity.class));
        });

        // 2. Nút Hỗ trợ vay vốn
        findViewById(R.id.btnLoanSupportMenu).setOnClickListener(v -> {
            startActivity(new Intent(this, LoanSupportActivity.class));
        });

        // 3. Nút Đăng ký bảng điểm (Mới thêm)
        findViewById(R.id.btnTranscriptMenu).setOnClickListener(v -> {
            startActivity(new Intent(this, TranscriptRegistrationActivity.class));
        });

        // 4. Nút Giấy xác nhận sinh viên (Mới thêm)
        findViewById(R.id.btnConfirmationMenu).setOnClickListener(v -> {
            startActivity(new Intent(this, StudentConfirmationActivity.class));
        });
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
            txtSectionTitle.setText(R.string.tab_results); // Dùng string resource cho chuyên nghiệp
            txtSectionTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_scroll_text, 0, 0, 0);
        }

        loadHistoryData();
    }

    private void loadHistoryData() {
        historyList.clear();
        long now = System.currentTimeMillis();

        // 1. Nạp mẫu: Cấp lại thẻ (Đã duyệt)
        historyList.add(new CardReissueService(
                getString(R.string.reissue_card_title),
                "Lý do: Thẻ bị hỏng chip", now, 1, "CARD_REISSUE",
                getString(R.string.default_name), getString(R.string.default_mssv), getString(R.string.default_class)));

        // 2. Nạp mẫu: Bảng điểm (Đang chờ) - MÔ PHỎNG CHỨC NĂNG MỚI
        historyList.add(new TranscriptService(
                getString(R.string.transcript_registration_title),
                "Số lượng: 03 bản", now - 3600000, 0, "TRANSCRIPT_REG",
                getString(R.string.default_name), getString(R.string.default_mssv), getString(R.string.default_class),
                "2023 - 2024", "Học kỳ 2", "03"));

        // 3. Nạp mẫu: Xác nhận sinh viên (Đã duyệt) - MÔ PHỎNG CHỨC NĂNG MỚI
        historyList.add(new StudentConfirmationService(
                getString(R.string.student_confirmation_title),
                "Lý do: Làm hồ sơ thực tập", now - 86400000, 1, "STUDENT_CONFIRM",
                getString(R.string.default_name), getString(R.string.default_mssv), getString(R.string.default_class)));

        // 4. Nạp mẫu: Hỗ trợ vay vốn (Đang chờ)
        historyList.add(new LoanSupportService(
                getString(R.string.loan_support_title),
                "Số tiền: 10.000.000đ", now - 172800000, 0, "LOAN_SUPPORT",
                "10000000", "Học kỳ 1", getString(R.string.default_phone)));

        // Thiết lập adapter để hiển thị lên RecyclerView
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
}