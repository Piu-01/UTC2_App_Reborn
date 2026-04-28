package com.utc2.appreborn.ui.tuition.Dorm;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.tuition.adapter.DormAdapter;
import com.utc2.appreborn.ui.tuition.model.DormTuition;
import com.utc2.appreborn.utils.NetworkUtils; // Import Utils
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DormitoryTuitionActivity extends AppCompatActivity {

    private RecyclerView rvDormTuition;
    private List<DormTuition> dormList;
    private TextView tvTotalDormAmount;
    private Button btnPayDorm;
    private long totalAmount = 0;

    // Quản lý trạng thái mạng
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dormitory_tuition);

        try {
            initViews();
            setupNetworkMonitoring(); // Theo dõi mạng
            loadDormData();
            calculateTotal();
            setupRecyclerView();
        } catch (Exception e) {
            Log.e("DormTuition", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        rvDormTuition = findViewById(R.id.rvDormTuition);
        tvTotalDormAmount = findViewById(R.id.tvTotalDormAmount);
        btnPayDorm = findViewById(R.id.btnPayDorm);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Expression Lambda giúp code gọn và hết báo vàng
        btnBack.setOnClickListener(v -> finish());

        // Kiểm tra mạng trước khi cho phép mở Dialog thanh toán
        btnPayDorm.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                showPaymentDialog();
            } else {
                Toast.makeText(this, "Cần kết nối mạng để tạo mã QR thanh toán!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                Log.d("Network", "Sẵn sàng thanh toán KTX");
            }

            @Override
            public void onNetworkLost() {
                Toast.makeText(DormitoryTuitionActivity.this,
                        "Mất kết nối! Vui lòng không thực hiện thanh toán lúc này.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    private void setupRecyclerView() {
        rvDormTuition.setLayoutManager(new LinearLayoutManager(this));
        DormAdapter adapter = new DormAdapter(dormList);
        rvDormTuition.setAdapter(adapter);
    }

    private void loadDormData() {
        dormList = new ArrayList<>();
        // Giả lập dữ liệu (Sau này lụm từ MySQL qua API)
        dormList.add(new DormTuition("Phòng 402 - Dãy B", "Tháng 03/2026 - Điện nước", 650000, 0));
        dormList.add(new DormTuition("Phòng 402 - Dãy B", "Tháng 02/2026 - Điện nước", 720000, 0));
        dormList.add(new DormTuition("Phòng 402 - Dãy B", "Tháng 01/2026 - Điện nước", 680000, 0));
    }

    private void calculateTotal() {
        totalAmount = 0;
        for (DormTuition item : dormList) {
            if (item.getStatus() == 0) {
                totalAmount += item.getAmount();
            }
        }

        String formattedPrice = String.format(Locale.getDefault(), "%,d VND", totalAmount);
        // Ưu tiên dùng string resource: <string name="label_total_format">Tổng cộng: %s</string>
        tvTotalDormAmount.setText(getString(R.string.label_total_format, formattedPrice));
    }

    private void showPaymentDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_payment_qr);

        Window window = dialog.getWindow();
        if (window != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.gravity = Gravity.CENTER;
            window.setAttributes(windowParams);
        }

        ImageView imgQr = dialog.findViewById(R.id.imgQrCode);
        TextView tvDialogAmount = dialog.findViewById(R.id.tvDialogAmount);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirmPayment);

        tvDialogAmount.setText(String.format(Locale.getDefault(), "%,d VND", totalAmount));

        // VietQR Config
        String bankId = "ICB";
        String accountNo = "102882730986";
        String accountName = "HINH%20VINH%20PHAT";
        String description = "AppReborn%20KTX%20P402B"; // Nội dung chuyển khoản

        String qrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accountNo + "-compact.png"
                + "?amount=" + totalAmount
                + "&addInfo=" + description
                + "&accountName=" + accountName;

        Glide.with(this)
                .load(qrUrl)
                .placeholder(R.drawable.logo_utc2)
                .into(imgQr);

        btnConfirm.setOnClickListener(v -> {
            // Chỗ này mốt bro sẽ gọi hàm check transaction từ ngân hàng
            Toast.makeText(this, getString(R.string.msg_checking_transaction), Toast.LENGTH_SHORT).show();
            btnConfirm.postDelayed(() -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    Toast.makeText(this, "Thanh toán KTX thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, 2000);
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkUtils != null) {
            networkUtils.unregister();
        }
    }
}