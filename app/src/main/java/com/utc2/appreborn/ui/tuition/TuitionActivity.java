package com.utc2.appreborn.ui.tuition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.tuition.Dorm.DormitoryTuitionActivity;
import com.utc2.appreborn.ui.tuition.Invoice.InvoiceActivity;
import com.utc2.appreborn.ui.tuition.Subject.SubjectTuitionActivity;

public class TuitionActivity extends AppCompatActivity {

    private CardView cardTuitionSubject, cardDormitory, cardInvoice;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuition);

        initViews();
        setClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        cardTuitionSubject = findViewById(R.id.cardTuitionSubject);
        cardDormitory = findViewById(R.id.cardDormitory);
        cardInvoice = findViewById(R.id.cardInvoice);
    }

    private void setClickListeners() {
        // Nút quay lại trang trước đó
        btnBack.setOnClickListener(v -> finish());

        // 1. Mở trang Học phí học phần
        cardTuitionSubject.setOnClickListener(v -> {
            Intent intent = new Intent(TuitionActivity.this, SubjectTuitionActivity.class);
            startActivity(intent);
        });

        // 2. Mở trang Tiền KTX
        cardDormitory.setOnClickListener(v -> {
            Intent intent = new Intent(TuitionActivity.this, DormitoryTuitionActivity.class);
            startActivity(intent);
        });

        // 3. Mở trang Lịch sử Hóa đơn
        cardInvoice.setOnClickListener(v -> {
            Intent intent = new Intent(TuitionActivity.this, InvoiceActivity.class);
            startActivity(intent);
        });
    }
}