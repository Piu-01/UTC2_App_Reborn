package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.login.LoginActivity;
import com.utc2.appreborn.ui.main.MainActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton back;

    private TextView tvName, tvEmail, tvClass,
            tvPhone, tvDOB, tvMSSV, tvAddress;

    private Button btnProgram, btnLogout, btnSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ check login trước
        if (!checkLogin()) return;

        setContentView(R.layout.activity_profile);

        initView();
        loadProfile();
        setupButton();
        setupBackButton();
    }

    // ================= LOGIN CHECK =================
    private boolean checkLogin() {

        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return false;
        }

        return true;
    }

    // ================= INIT VIEW =================
    private void initView() {

        back = findViewById(R.id.btnBack);

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

        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

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

        btnProgram.setOnClickListener(v ->
                startActivity(new Intent(
                        ProfileActivity.this,
                        TrainingProgramActivity.class)));

        btnSupport.setOnClickListener(v ->
                startActivity(new Intent(
                        ProfileActivity.this,
                        SupportActivity.class)));

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

    // ================= BACK BUTTON =================
    private void setupBackButton() {

        back.setOnClickListener(v -> {
            startActivity(new Intent(
                    ProfileActivity.this,
                    MainActivity.class
            ));
            finish();
        });
    }
}