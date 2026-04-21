package com.utc2.appreborn.ui.public_services.CardReissueService;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;

public class CardReissueActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView btnConfirm, txtName, txtMSSV, txtClass;
    private EditText edtReason; // Thêm biến cho ô nhập lý do

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_reissue);

        initViews();
        setupData();
        setupEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);
        txtName = findViewById(R.id.txtName);
        txtMSSV = findViewById(R.id.txtMSSV);
        txtClass = findViewById(R.id.txtClass);
        // Ánh xạ ô nhập lý do (đảm bảo trong XML ID là edtReason)
        edtReason = findViewById(R.id.edtReason);
    }

    private void setupData() {
        // Hiển thị dữ liệu mặc định từ strings.xml
        txtName.setText(getString(R.string.default_name));
        txtMSSV.setText(getString(R.string.default_mssv));
        txtClass.setText(getString(R.string.default_class));
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            try {
                // 1. Lấy dữ liệu từ giao diện
                String name = txtName.getText().toString();
                String mssv = txtMSSV.getText().toString();
                String className = txtClass.getText().toString();
                String reason = edtReason.getText().toString().trim();

                // 2. Logic "Không bắt buộc": Nếu trống thì tự gán nội dung mặc định
                String finalReason = reason.isEmpty() ? "Không có lý do cụ thể" : reason;

                // 3. Giả lập tạo Object để gửi đi (Đủ 8 tham số như Model đã sửa)
                // CardReissueService(Title, Description, Timestamp, Status, Type, Name, ID, ClassName)
                CardReissueService newRequest = new CardReissueService(
                        getString(R.string.reissue_card_title),
                        finalReason,
                        System.currentTimeMillis(),
                        0, // Trạng thái: Đang xử lý
                        "CARD_REISSUE",
                        name,
                        mssv,
                        className
                );

                // --- GỌI HÀM GỬI LÊN SERVER/FIREBASE TẠI ĐÂY ---
                // sendToFirebase(newRequest);

                Toast.makeText(this, R.string.cardReissue_registration_success, Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}