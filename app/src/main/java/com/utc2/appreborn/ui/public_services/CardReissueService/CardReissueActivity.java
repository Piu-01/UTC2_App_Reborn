package com.utc2.appreborn.ui.public_services.CardReissueService;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.public_services.model.CardReissueService;
import com.utc2.appreborn.utils.NetworkUtils; // Import Utils

public class CardReissueActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView btnConfirm, txtName, txtMSSV, txtClass;
    private EditText edtReason;

    // Quản lý trạng thái mạng
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_reissue);

        try {
            initViews();
            setupData();
            setupNetworkMonitoring(); // Khởi tạo lắng nghe mạng
            setupEvents();
        } catch (Exception e) {
            Log.e("CardReissue", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);
        txtName = findViewById(R.id.txtName);
        txtMSSV = findViewById(R.id.txtMSSV);
        txtClass = findViewById(R.id.txtClass);
        edtReason = findViewById(R.id.edtReason);
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                // Có thể làm nút xác nhận sáng lên hoặc đổi màu khi có mạng
                Log.d("Network", "Sẵn sàng gửi yêu cầu");
            }

            @Override
            public void onNetworkLost() {
                // Cảnh báo ngay lập tức nếu đang trong màn hình này mà mất mạng
                Toast.makeText(CardReissueActivity.this,
                        "Mất kết nối mạng. Bạn không thể gửi yêu cầu lúc này.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    private void setupData() {
        txtName.setText(getString(R.string.default_name));
        txtMSSV.setText(getString(R.string.default_mssv));
        txtClass.setText(getString(R.string.default_class));
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            // KIỂM TRA MẠNG TRƯỚC KHI XỬ LÝ DỮ LIỆU
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Không có mạng, không thể gửi đơn đăng ký!", Toast.LENGTH_SHORT).show();
                return; // Dừng lại luôn, không chạy code bên dưới
            }

            try {
                String name = txtName.getText().toString();
                String mssv = txtMSSV.getText().toString();
                String className = txtClass.getText().toString();
                String reason = edtReason.getText().toString().trim();

                String finalReason = reason.isEmpty() ? "Không có lý do cụ thể" : reason;

                // Giả lập tạo Object
                CardReissueService newRequest = new CardReissueService(
                        getString(R.string.reissue_card_title),
                        finalReason,
                        System.currentTimeMillis(),
                        0,
                        "CARD_REISSUE",
                        name,
                        mssv,
                        className
                );

                // --- GỌI API GỬI LÊN SERVER TẠI ĐÂY ---
                // handleSendRequest(newRequest);

                Toast.makeText(this, R.string.cardReissue_registration_success, Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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