package com.utc2.appreborn.ui.public_services.StudentConfirmationService;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.public_services.model.StudentConfirmationService;
import com.utc2.appreborn.utils.NetworkUtils; // Import Utils

public class StudentConfirmationActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView txtName, txtMSSV, txtClass;
    private EditText edtReason;
    private TextView btnConfirm;

    // Quản lý trạng thái mạng
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_confirmation);

        try {
            initViews();
            setupData();
            setupNetworkMonitoring(); // Khởi tạo lắng nghe mạng
            setupEvents();
        } catch (Exception e) {
            Log.e("StudentConfirmation", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        txtName = findViewById(R.id.txtName);
        txtMSSV = findViewById(R.id.txtMSSV);
        txtClass = findViewById(R.id.txtClass);
        edtReason = findViewById(R.id.edtReason);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                Log.d("Network", "Hệ thống sẵn sàng tiếp nhận yêu cầu xác nhận");
            }

            @Override
            public void onNetworkLost() {
                // Thông báo để sinh viên biết mạng đang gặp sự cố
                Toast.makeText(StudentConfirmationActivity.this,
                        "Mất kết nối mạng. Vui lòng kiểm tra lại để đảm bảo yêu cầu được gửi đi.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    private void setupData() {
        // Hiển thị thông tin từ resources
        txtName.setText(getString(R.string.default_name));
        txtMSSV.setText(getString(R.string.default_mssv));
        txtClass.setText(getString(R.string.default_class));
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            // KIỂM TRA MẠNG TRƯỚC KHI GỬI
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Không có mạng! Vui lòng kết nối để gửi yêu cầu xác nhận.", Toast.LENGTH_SHORT).show();
                return;
            }

            String reason = edtReason.getText().toString().trim();

            if (reason.isEmpty()) {
                edtReason.setError("Bạn cần nhập lý do (VD: Bổ sung hồ sơ thực tập)");
                return;
            }

            try {
                // Khởi tạo đối tượng gửi đi
                StudentConfirmationService newRequest = new StudentConfirmationService(
                        getString(R.string.student_confirmation_title),
                        reason,
                        System.currentTimeMillis(),
                        0, // Trạng thái: Đang chờ duyệt
                        "STUDENT_CONFIRM",
                        txtName.getText().toString(),
                        txtMSSV.getText().toString(),
                        txtClass.getText().toString()
                );

                // --- GỌI API GỬI LÊN SERVER/FIREBASE TẠI ĐÂY ---
                // api.submitConfirmationRequest(newRequest);

                Toast.makeText(this, "Đăng ký giấy xác nhận thành công!", Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký để tránh memory leak
        if (networkUtils != null) {
            networkUtils.unregister();
        }
    }
}