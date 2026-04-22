package com.utc2.appreborn.ui.main;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.results.AcademicResultsFragment;

/**
 * MainActivity
 *
 * - EdgeToEdge enabled → status bar transparent, content goes full screen.
 *   Mỗi Fragment tự xử lý padding top via ViewCompat.setOnApplyWindowInsetsListener.
 * - nav_result → AcademicResultsFragment (dashboard 4 thẻ).
 * - navigateTo() dùng để push sub-fragment từ Dashboard.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Bật edge-to-edge để nội dung vẽ sau status bar (trong suốt)
        EdgeToEdge.enable(this);

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

        // Load màn hình mặc định
        if (savedInstanceState == null) {
            loadFragment(new AcademicResultsFragment());
        }
    }

    // ─────────────────────────────────────────
    // Bottom Nav routing
    // ─────────────────────────────────────────
    private void handleNavigation(int id) {
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            // fragment = new HomeFragment();
        } else if (id == R.id.nav_schedule) {
            // fragment = new ScheduleFragment();
        } else if (id == R.id.nav_register) {
            // fragment = new RegisterFragment();
        } else if (id == R.id.nav_result) {
            fragment = new AcademicResultsFragment();   // ✅ Dashboard KQHT
        } else if (id == R.id.nav_profile) {
            // fragment = new ProfileFragment();
        }

        if (fragment != null) {
            // Khi click bottom nav → xóa back stack, quay về dashboard
            getSupportFragmentManager().popBackStack(null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            loadFragment(fragment);
        }
    }

    /**
     * Replace fragment KHÔNG thêm vào back stack.
     * Dùng cho bottom nav (root destinations).
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    /**
     * Push fragment vào back stack.
     * Dùng cho điều hướng từ Dashboard → sub-screen.
     * Được gọi từ AcademicResultsFragment.
     */
    public void navigateTo(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
