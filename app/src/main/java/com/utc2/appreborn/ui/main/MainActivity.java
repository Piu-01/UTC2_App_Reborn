// PATH: app/src/main/java/com/utc2/appreborn/ui/main/MainActivity.java
// ⚠️ Thay thế file hiện có. Thêm xử lý nav_assessment.

package com.utc2.appreborn.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;
import android.os.Bundle;
import android.view.MenuItem;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.assessment.AssessmentFragment;
import com.utc2.appreborn.ui.schedule.ScheduleFragment;

import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ComposeView bottomBarCompose = findViewById(R.id.bottom_bar_compose);

        LiquidBarKt.setupLiquidBottomBar(
                bottomBarCompose,
                id -> {
                    handleNavigation(id);
                    return null;
                }
        );
    }

    private void handleNavigation(int id) {
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            // fragment = new HomeFragment();
        } else if (id == R.id.nav_schedule) {
            fragment = new ScheduleFragment();
        } else if (id == R.id.nav_register) {
            // fragment = new RegisterFragment();
        } else if (id == R.id.nav_assessment) {           // ✅ THÊM MỚI
            fragment = new AssessmentFragment();
        } else if (id == R.id.nav_result) {
            // fragment = new ResultFragment();
        } else if (id == R.id.nav_profile) {
            // fragment = new ProfileFragment();
        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}