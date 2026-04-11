package com.utc2.appreborn.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;

public class GraduationRequirementsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graduation_requirements);

        // Ánh xạ nút quay về từ XML
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Thiết lập sự kiện click để quay lại trang Profile
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng Activity này để quay về Activity trước đó (Profile)
                finish();
            }
        });
    }
}