package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.login.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvClass,
            tvPhone, tvDOB, tvMSSV, tvAddress;

    private Button btnProgram, btnLogout, btnSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ kiểm tra login trước
        if (!checkLogin()) return;

        setContentView(R.layout.activity_profile);

        initView();
        loadProfile();
        setupButton();
    }

    // ================= LOGIN CHECK =================
    private boolean checkLogin() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return false;
        }

        return true;
    }

    // ================= INIT VIEW =================
    private void initView() {

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvClass = findViewById(R.id.tvClass);
        tvPhone = findViewById(R.id.tvPhone);
        tvDOB = findViewById(R.id.tvDOB);
        tvMSSV = findViewById(R.id.tvMSSV);
        tvAddress = findViewById(R.id.tvAddress);

        btnProgram = findViewById(R.id.btnProgram);
        btnLogout = findViewById(R.id.btnLogout);

        btnSupport = findViewById(R.id.btnSupport);
    }

    // ================= LOAD DATA =================
    private void loadProfile() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String email = (user != null && user.getEmail() != null)
                ? user.getEmail()
                : getString(R.string.default_email);

        tvName.setText(getString(
                R.string.label_name,
                getString(R.string.default_name)));

        tvEmail.setText(getString(
                R.string.label_email,
                email));

        tvClass.setText(getString(
                R.string.label_class,
                getString(R.string.default_class)));

        tvPhone.setText(getString(
                R.string.label_phone,
                getString(R.string.default_phone)));

        tvDOB.setText(getString(
                R.string.label_dob,
                getString(R.string.default_dob)));

        tvMSSV.setText(getString(
                R.string.label_mssv,
                getString(R.string.default_mssv)));

        tvAddress.setText(getString(
                R.string.label_address,
                getString(R.string.default_address)));
    }

    // ================= BUTTON =================
    private void setupButton() {

        //  chương trình đào tạo
        btnProgram.setOnClickListener(v ->
                startActivity(new Intent(
                        ProfileActivity.this,
                        TrainingProgramActivity.class)));

        //  hỗ trợ
        if (btnSupport != null) {
            btnSupport.setOnClickListener(v ->
                    startActivity(new Intent(
                            ProfileActivity.this,
                            SupportActivity.class)));
        }

        // 👉 logout
        btnLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent =
                    new Intent(ProfileActivity.this,
                            LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }
}