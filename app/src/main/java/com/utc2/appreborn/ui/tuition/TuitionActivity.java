package com.utc2.appreborn.ui.tuition;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.tuition.Dorm.DormitoryTuitionActivity;
import com.utc2.appreborn.ui.tuition.Invoice.InvoiceActivity;
import com.utc2.appreborn.ui.tuition.Subject.SubjectTuitionActivity;
import com.utc2.appreborn.utils.NetworkUtils;

public class TuitionActivity extends AppCompatActivity {

    private CardView cardTuitionSubject, cardDormitory, cardInvoice;
    private ImageButton btnBack;
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuition);

        try {
            initViews();
            setupNetworkMonitoring();
            setClickListeners();
        } catch (Exception e) {
            Log.e("TuitionActivity", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        cardTuitionSubject = findViewById(R.id.cardTuitionSubject);
        cardDormitory = findViewById(R.id.cardDormitory);
        cardInvoice = findViewById(R.id.cardInvoice);
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                Log.d("Network", "Sẵn sàng kết nối dữ liệu tài chính");
            }

            @Override
            public void onNetworkLost() {
                Toast.makeText(TuitionActivity.this,
                        "Bạn đang ngoại tuyến. Dữ liệu học phí có thể không chính xác.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    private void setClickListeners() {
        // Sử dụng Expression Lambda (v -> finish()) để xóa cảnh báo màu vàng
        btnBack.setOnClickListener(v -> finish());

        // Gom nhóm điều hướng vào hàm check mạng cho sạch code
        cardTuitionSubject.setOnClickListener(v -> checkNetworkAndNavigate(SubjectTuitionActivity.class));
        cardDormitory.setOnClickListener(v -> checkNetworkAndNavigate(DormitoryTuitionActivity.class));
        cardInvoice.setOnClickListener(v -> checkNetworkAndNavigate(InvoiceActivity.class));
    }

    /**
     * Hàm điều hướng có kiểm tra mạng - Đảm bảo dữ liệu tài chính luôn được load mới
     */
    private void checkNetworkAndNavigate(Class<?> targetActivity) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, targetActivity));
        } else {
            Toast.makeText(this, "Vui lòng kết nối mạng để xem thông tin học phí!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkUtils != null) {
            networkUtils.unregister();
        }
    }
}