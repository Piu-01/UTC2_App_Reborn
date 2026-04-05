package com.utc2.appreborn.ui.tuition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.main.MainActivity;

public class TuitionActivity extends AppCompatActivity {

    ImageButton btnBack;
    Button btnViewTuition, btnPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuition);

        btnBack = findViewById(R.id.btnBack);
        btnViewTuition = findViewById(R.id.btnViewTuition);
        btnPayment = findViewById(R.id.btnPayment);

        // Quay về Main
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Xem học phí
        btnViewTuition.setOnClickListener(v -> {
            startActivity(new Intent(this, TuitionResultActivity.class));
            finish();
        });

        // Thanh toán
        btnPayment.setOnClickListener(v -> {
            // TODO: mở trang thanh toán
        });
    }
}