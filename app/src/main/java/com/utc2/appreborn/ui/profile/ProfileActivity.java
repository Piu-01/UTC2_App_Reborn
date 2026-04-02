package com.utc2.appreborn.ui.profile;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utc2.appreborn.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvClass, tvPhone, tvDOB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();
        loadProfile();
    }

    // ✅ Ánh xạ view
    private void initView() {
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvClass = findViewById(R.id.tvClass);
        tvPhone = findViewById(R.id.tvPhone);
        tvDOB = findViewById(R.id.tvDOB);
    }

    // ✅ Load dữ liệu profile
    private void loadProfile() {

        // Dữ liệu mặc định
        String name = getString(R.string.default_name);
        String studentClass = getString(R.string.default_class);
        String phone = getString(R.string.default_phone);
        String dob = getString(R.string.default_dob);

        // Email Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String email;
        if (user != null && user.getEmail() != null) {
            email = user.getEmail();
        } else {
            email = getString(R.string.default_email);
        }

        // Hiển thị
        tvName.setText(getString(R.string.label_name, name));
        tvEmail.setText(getString(R.string.label_email, email));
        tvClass.setText(getString(R.string.label_class, studentClass));
        tvPhone.setText(getString(R.string.label_phone, phone));
        tvDOB.setText(getString(R.string.label_dob, dob));
    }
}