package com.utc2.appreborn.ui.tuition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.main.MainActivity;

public class TuitionResultActivity extends AppCompatActivity {

    ImageButton btnBack, btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuition_result);

        // ánh xạ view
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);

        // nút quay lại Tuition
        btnBack.setOnClickListener(v -> finish());

        // nút HOME → MainActivity
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(TuitionResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}