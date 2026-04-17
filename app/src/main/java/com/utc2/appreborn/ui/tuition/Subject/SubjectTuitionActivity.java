package com.utc2.appreborn.ui.tuition.Subject;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager; // Thêm import này
import android.widget.Button;
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

public class SubjectTuitionActivity extends AppCompatActivity {

    private RecyclerView rvItems;
    private List<SubjectTuition> subjectList;
    private TextView tvTotalAmount;
    private Button btnPay;
    private long totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_tuition);

        initViews();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadData();
        calculateTotal();

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        // Sửa cảnh báo field can be converted to local variable bằng cách khai báo trực tiếp
        SubjectTuitionAdapter adapter = new SubjectTuitionAdapter(subjectList);
        rvItems.setAdapter(adapter);

        btnPay.setOnClickListener(v -> showPaymentDialog());
    }

    private void initViews() {
        rvItems = findViewById(R.id.rvItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnPay = findViewById(R.id.btnPay);
    }

    private void loadData() {
        subjectList = new ArrayList<>();
        subjectList.add(new SubjectTuition(1, "Lập trình Android", "3 tín chỉ", 1250000, 0));
        subjectList.add(new SubjectTuition(2, "Cấu trúc dữ liệu", "4 tín chỉ", 1600000, 0));
        subjectList.add(new SubjectTuition(3, "Anh văn chuyên ngành", "2 tín chỉ", 850000, 0));
    }

    private void calculateTotal() {
        totalAmount = 0;
        for (SubjectTuition item : subjectList) {
            if (item.getStatus() == 0) {
                totalAmount += item.getAmount();
            }
        }
        tvTotalAmount.setText(String.format(Locale.getDefault(), "%,d VND", totalAmount));
    }

    private void showPaymentDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_payment_qr);

        Window window = dialog.getWindow();
        if (window != null) {
            // FIX LỆCH: Thiết lập chiều rộng bằng 90% màn hình và canh giữa tuyệt đối
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.gravity = Gravity.CENTER; // Ép vào giữa
            window.setAttributes(windowParams);
        }

        ImageView imgQr = dialog.findViewById(R.id.imgQrCode);
        TextView tvDialogAmount = dialog.findViewById(R.id.tvDialogAmount);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirmPayment);

        tvDialogAmount.setText(String.format(Locale.getDefault(), "%,d VND", totalAmount));

        String bankId = "ICB";
        String accountNo = "102882730986";
        String accountName = "HINH%20VINH%20PHAT";
        String description = "Nop%20hoc%20phi%20AppReborn";

        String qrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accountNo + "-compact.png"
                + "?amount=" + totalAmount
                + "&addInfo=" + description
                + "&accountName=" + accountName;

        Glide.with(this)
                .load(qrUrl)
                .placeholder(R.drawable.logo_utc2)
                .into(imgQr);

        btnConfirm.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.msg_checking_transaction), Toast.LENGTH_SHORT).show();
            btnConfirm.postDelayed(() -> {
                dialog.dismiss();
                Toast.makeText(this, getString(R.string.msg_payment_success), Toast.LENGTH_SHORT).show();
                finish();
            }, 1500);
        });

        dialog.show();
    }
}