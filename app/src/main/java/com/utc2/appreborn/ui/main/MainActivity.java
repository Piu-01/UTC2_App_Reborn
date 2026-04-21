package com.utc2.appreborn.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;
import android.os.Bundle;
import android.widget.Toast;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.utc2.appreborn.ui.schedule.ScheduleFragment;
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
        } else if (id == R.id.nav_result) {
            // fragment = new ResultFragment();
        } else if (id == R.id.nav_profile) {
            // fragment = new ProfileFragment();
        }

        // 🔹 QUAN TRỌNG: Lệnh thực hiện chuyển trang
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment) // R.id.fragment_container là ID trong file XML ở trên
                    .commit();
        } /// NHớ bổ sung cho nếu null thì quay về trang home...
    }
}