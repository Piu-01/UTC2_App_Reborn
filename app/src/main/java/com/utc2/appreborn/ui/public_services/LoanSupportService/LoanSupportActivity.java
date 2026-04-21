package com.utc2.appreborn.ui.public_services.LoanSupportService;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.utc2.appreborn.R;

public class LoanSupportActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView btnConfirm;
    private EditText edtAmount, edtReason, edtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_support);

        // Ánh xạ View
        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirmLoan);
        edtAmount = findViewById(R.id.edtAmount);
        edtReason = findViewById(R.id.edtReason);
        edtPhone = findViewById(R.id.edtPhone);

        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> handleLoanRegistration());
    }

    private void handleLoanRegistration() {
        // Lấy dữ liệu và xóa khoảng trắng thừa
        String amount = edtAmount.getText().toString().trim();
        String reason = edtReason.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // Kiểm tra nếu có ô bị để trống
        if (amount.isEmpty() || reason.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Khởi tạo đối tượng yêu cầu vay vốn
            LoanSupportService request = new LoanSupportService(
                    getString(R.string.loan_support_title),
                    reason,
                    System.currentTimeMillis(),
                    0, // 0: Đang xử lý
                    "LOAN_SUPPORT",
                    amount,
                    reason,
                    phone
            );

            // Giả lập xử lý gửi dữ liệu (API/Database)
            // Ví dụ: databaseHelper.insert(request);

            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình khi thành công

        } catch (Exception e) {
            // Xử lý khi có lỗi xảy ra (ví dụ: lỗi kết nối, lỗi bộ nhớ)
            Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}