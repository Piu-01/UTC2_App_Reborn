package com.utc2.appreborn.ui.tuition.Subject;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.tuition.adapter.SubjectTuitionAdapter;
import com.utc2.appreborn.ui.tuition.model.SubjectTuition;
import com.utc2.appreborn.utils.NetworkUtils;
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

        try {
            initViews();
            loadData();
            calculateTotal();
            setupRecyclerView();
        } catch (Exception e) {
            Log.e("SubjectTuition", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        rvItems = findViewById(R.id.rvItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnPay = findViewById(R.id.btnPay);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnPay.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                showPaymentDialog();
            } else {
                Toast.makeText(this, "Vui lòng kết nối mạng để tạo mã thanh toán!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        subjectList = new ArrayList<>();
        subjectList.add(new SubjectTuition(1, "Lập trình Android", "3 tín chỉ", 1250000, 0));
        subjectList.add(new SubjectTuition(2, "Cấu trúc dữ liệu", "4 tín chỉ", 1600000, 0));
        subjectList.add(new SubjectTuition(3, "Anh văn chuyên ngành", "2 tín chỉ", 850000, 0));
    }

    private void calculateTotal() {
        totalAmount = 0;
        for (SubjectTuition subject : subjectList) {
            if (subject.getStatus() == 0) { // Chỉ tính những môn chưa thanh toán
                totalAmount += subject.getAmount();
            }
        }
        tvTotalAmount.setText(String.format(Locale.getDefault(), "%,d VND", totalAmount));
    }

    private void setupRecyclerView() {
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        SubjectTuitionAdapter adapter = new SubjectTuitionAdapter(subjectList);
        rvItems.setAdapter(adapter);
    }

    private void showPaymentDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_payment_qr);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView imgQr = dialog.findViewById(R.id.imgQrCode);
        TextView txtAmount = dialog.findViewById(R.id.tvAmount);

        txtAmount.setText(String.format(Locale.getDefault(), "Số tiền: %,d VND", totalAmount));

        String bankId = "ICB";
        String accountNo = "102882730986";
        String accountName = "HINH%20VINH%20PHAT";
        String description = "AppReborn%20Hoc%20phi%20mon%20hoc";

        String qrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accountNo + "-compact.png"
                + "?amount=" + totalAmount
                + "&addInfo=" + description
                + "&accountName=" + accountName;

        Glide.with(this).load(qrUrl).into(imgQr);

        dialog.findViewById(R.id.btnConfirmPayment).setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(this, "Yêu cầu thanh toán đang được xử lý!", Toast.LENGTH_LONG).show();
        });

        dialog.show();
    }
}