package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.login.LoginActivity;
import com.utc2.appreborn.ui.main.MainActivity;

public class InfoActivity extends AppCompatActivity {

    private ImageView btnBack, imgStudentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check login
        if (!checkLogin()) return;

        setContentView(R.layout.activity_info);

        initView();
        setupButton();
        setupBackButton();
    }

    // ================= LOGIN CHECK =================
    private boolean checkLogin() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return false;
        }
        return true;
    }

    // ================= INIT VIEW =================
    private void initView() {
        btnBack = findViewById(R.id.btnBack);
        imgStudentCard = findViewById(R.id.imgStudentCard);
    }

    // ================= BUTTON FUNCTION =================
    private void setupButton() {

        // Nút chương trình đào tạo
        findViewById(R.id.btnTrainingProgram).setOnClickListener(v ->
                startActivity(new Intent(InfoActivity.this,
                        TrainingProgramActivity.class))
        );

        // Nút hỗ trợ
        findViewById(R.id.btnSupport).setOnClickListener(v ->
                startActivity(new Intent(InfoActivity.this,
                        SupportActivity.class))
        );

        // Logout
        findViewById(R.id.btnLogout).setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(InfoActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }

    // ================= BACK BUTTON =================
    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(InfoActivity.this, MainActivity.class));
            finish();
        });
    }
}