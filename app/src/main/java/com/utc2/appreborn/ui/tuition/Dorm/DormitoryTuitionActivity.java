package com.utc2.appreborn.ui.tuition.Dorm;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DormitoryTuitionActivity extends AppCompatActivity {

    private RecyclerView rvDormTuition;
    private List<DormTuition> dormList;
    private TextView tvTotalDormAmount;
    private Button btnPayDorm;
    private long totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dormitory_tuition);

        // 1. Ánh xạ View
        rvDormTuition = findViewById(R.id.rvDormTuition);
        tvTotalDormAmount = findViewById(R.id.tvTotalDormAmount);
        btnPayDorm = findViewById(R.id.btnPayDorm);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // 2. Nút Back
        btnBack.setOnClickListener(v -> finish());

        // 3. Load dữ liệu và tính tổng tiền
        loadDormData();
        calculateTotal();

        // 4. Thiết lập RecyclerView
        rvDormTuition.setLayoutManager(new LinearLayoutManager(this));
        // Khai báo adapter cục bộ để tránh cảnh báo "Field can be converted to local variable"
        DormAdapter adapter = new DormAdapter(dormList);
        rvDormTuition.setAdapter(adapter);

        // 5. Nút Thanh toán
        btnPayDorm.setOnClickListener(v -> showPaymentDialog());
    }

    private void loadDormData() {
        dormList = new ArrayList<>();

        // Cấu trúc mới: new DormTuition(name, details, amount, status)
        // 'name' ở đây đóng vai trò là số phòng
        dormList.add(new DormTuition("Phòng 402 - Dãy B", "Tháng 03/2026 - Tiền phòng + Điện nước", 650000, 0));
        dormList.add(new DormTuition("Phòng 402 - Dãy B", "Tháng 02/2026 - Tiền phòng + Điện nước", 720000, 0));
        dormList.add(new DormTuition("Phòng 402 - Dãy B", "Tháng 01/2026 - Tiền phòng + Điện nước", 680000, 0));
    }
    private void calculateTotal() {
        totalAmount = 0;
        for (DormTuition item : dormList) {
            if (item.getStatus() == 0) {
                totalAmount += item.getAmount();
            }
        }

        // CẬP NHẬT: Sử dụng String Resource để tránh lỗi Hardcoded
        if (tvTotalDormAmount != null) {
            String formattedPrice = String.format(Locale.getDefault(), "%,d VND", totalAmount);
            tvTotalDormAmount.setText(getString(R.string.label_total_format, formattedPrice));
        }
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

        // Thông tin VietinBank của bạn
        String bankId = "ICB";
        String accountNo = "102882730986";
        String accountName = "HINH%20VINH%20PHAT";
        String description = "Nop%20tien%20KTX%20P402B";

        String qrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accountNo + "-compact.png"
                + "?amount=" + totalAmount
                + "&addInfo=" + description
                + "&accountName=" + accountName;

        Glide.with(this)
                .load(qrUrl)
                .placeholder(R.drawable.logo_utc2)
                .into(imgQr);

        btnConfirm.setOnClickListener(v -> {
            Toast.makeText(this, "Đang kiểm tra giao dịch...", Toast.LENGTH_SHORT).show();
            btnConfirm.postDelayed(() -> {
                dialog.dismiss();
                Toast.makeText(this, "Thanh toán KTX thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }, 1500);
        });

        dialog.show();
    }
}