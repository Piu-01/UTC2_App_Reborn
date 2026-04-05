package com.utc2.appreborn.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.profile.ProfileActivity;
import com.utc2.appreborn.ui.tuition.TuitionActivity;

import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    Button btnProfile, btnTuition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Đã vào MainActivity", Toast.LENGTH_LONG).show();

        // Liquid Bar
        ComposeView bottomBarCompose = findViewById(R.id.bottom_bar_compose);

        LiquidBarKt.setupLiquidBottomBar(
                bottomBarCompose,
                id -> {
                    handleNavigation(id);
                    return null;
                }
        );

        // Profile
        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Tuition
        btnTuition = findViewById(R.id.btnTuition);
        btnTuition.setOnClickListener(v -> {
            startActivity(new Intent(this, TuitionActivity.class));
        });
    }

    private void handleNavigation(int id) {

        Fragment fragment = null;

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_schedule) {

        } else if (id == R.id.nav_register) {

        } else if (id == R.id.nav_result) {

        } else if (id == R.id.nav_profile) {

        }
    }
}