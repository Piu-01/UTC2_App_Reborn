package com.utc2.appreborn.ui.public_services.LoanSupportService;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.public_services.model.LoanSupportService;
import com.utc2.appreborn.utils.NetworkUtils; // Import Utils

public class LoanSupportActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView btnConfirm;
    private EditText edtAmount, edtReason, edtPhone;

    // Quản lý trạng thái mạng
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_support);

        try {
            initViews();
            setupNetworkMonitoring(); // Khởi tạo lắng nghe mạng
            setupEvents();
        } catch (Exception e) {
            Log.e("LoanSupport", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirmLoan);
        edtAmount = findViewById(R.id.edtAmount);
        edtReason = findViewById(R.id.edtReason);
        edtPhone = findViewById(R.id.edtPhone);
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                Log.d("Network", "Sẵn sàng gửi hồ sơ vay vốn");
            }

            @Override
            public void onNetworkLost() {
                // Cảnh báo ngay lập tức nếu đang nhập liệu mà mất mạng
                Toast.makeText(LoanSupportActivity.this,
                        "Mất kết nối mạng! Vui lòng kiểm tra lại để gửi đơn thành công.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> handleLoanRegistration());
    }

    private void handleLoanRegistration() {
        // CHỐT CHẶN 1: Kiểm tra mạng trước khi làm bất cứ việc gì
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Không có kết nối mạng. Không thể gửi đơn lúc này!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy dữ liệu và xóa khoảng trắng thừa
        String amount = edtAmount.getText().toString().trim();
        String reason = edtReason.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // CHỐT CHẶN 2: Kiểm tra tính hợp lệ của dữ liệu
        if (amount.isEmpty() || reason.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng số điện thoại cơ bản (tùy chọn thêm)
        if (phone.length() < 10) {
            edtPhone.setError("Số điện thoại không hợp lệ");
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

            // --- GỌI API GỬI LÊN SERVER TẠI ĐÂY ---
            // apiService.sendLoanRequest(request)...

            Toast.makeText(this, "Đăng ký thành công. Nhà trường sẽ liên hệ qua SĐT của bạn!", Toast.LENGTH_LONG).show();
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Luôn hủy đăng ký để tránh leak memory và tốn tài nguyên ngầm
        if (networkUtils != null) {
            networkUtils.unregister();
        }
    }
}