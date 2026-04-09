//package com.utc2.appreborn.ui.main;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.compose.ui.platform.ComposeView;
//import android.os.Bundle;
//import android.widget.Toast;
//import com.utc2.appreborn.R;
//import com.utc2.appreborn.ui.components.LiquidBarKt;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.PopupMenu;
//import androidx.fragment.app.Fragment;
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Toast.makeText(this, "Đã vào MainActivity", Toast.LENGTH_LONG).show();
//
//        ComposeView bottomBarCompose = findViewById(R.id.bottom_bar_compose);
//
//        LiquidBarKt.setupLiquidBottomBar(
//                bottomBarCompose,
//                id -> {
//                    handleNavigation(id);
//                    return null;
//                }
//        );
//    }
//    private void handleNavigation(int id) {
//
////        vd như ở class quản ly trang đó public class SettingFragment extends Fragment {
////            public SettingFragment() {
////                super(R.layout.fragment_setting);
////            }
////        }
//        // 🔹 B1: Tạo biến Fragment để chứa màn hình cần chuyển
//        Fragment fragment = null;
//
//        // 🔹 B2: Dựa vào ID nhận được từ LiquidBar để chọn màn hình tương ứng
//        // 👉 Lưu ý: ID này phải trùng với ID trong file menu (bottom_nav_menu.xml)
//        if (id == R.id.nav_home) {
//            //fragment = new HomeFragment();
//        } else if (id == R.id.nav_schedule) {
//          //  fragment = new ScheduleFragment();
//        } else if (id == R.id.nav_register) {
//          //  fragment = new RegisterFragment();
//        } else if (id == R.id.nav_result) {
//          //  fragment = new ResultFragment();
//        } else if (id == R.id.nav_profile) {
//           // fragment = new ProfileFragment();
//        } else {
//           // fragment = new HomeFragment(); // fallback
//        }
//
//    }
//}
package com.utc2.appreborn.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    // ─────────────────────────────────────────────
    // Fragment tags — used to find existing instances
    // ─────────────────────────────────────────────
    private static final String TAG_HOME     = "tag_home";
    private static final String TAG_SCHEDULE = "tag_schedule";
    private static final String TAG_REGISTER = "tag_register";
    private static final String TAG_RESULT   = "tag_result";
    private static final String TAG_PROFILE  = "tag_profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Wire up the Liquid bottom nav
        ComposeView bottomBarCompose = findViewById(R.id.bottom_bar_compose);
        LiquidBarKt.setupLiquidBottomBar(bottomBarCompose, id -> {
            handleNavigation(id);
            return null;
        });

        // Show HomeFragment on first launch
        if (savedInstanceState == null) {
            showFragment(new HomeFragment(), TAG_HOME);
        }
    }

    // ─────────────────────────────────────────────
    //  Navigation router
    // ─────────────────────────────────────────────
    private void handleNavigation(int id) {
        if (id == R.id.nav_home) {
            switchToFragment(HomeFragment.class, TAG_HOME);

        } else if (id == R.id.nav_schedule) {
            // TODO: Replace with ScheduleFragment when ready
            // switchToFragment(ScheduleFragment.class, TAG_SCHEDULE);

        } else if (id == R.id.nav_register) {
            // TODO: Replace with RegisterFragment when ready
            // switchToFragment(RegisterFragment.class, TAG_REGISTER);

        } else if (id == R.id.nav_result) {
            // TODO: Replace with ResultFragment when ready
            // switchToFragment(ResultFragment.class, TAG_RESULT);

        } else if (id == R.id.nav_profile) {
            // TODO: Replace with ProfileFragment when ready
            // switchToFragment(ProfileFragment.class, TAG_PROFILE);
        }
    }

    // ─────────────────────────────────────────────
    //  Helper: show / reuse fragment by tag
    //  Re-uses existing instance if it is already in
    //  the back-stack (avoids unnecessary recreation).
    // ─────────────────────────────────────────────
    private <T extends Fragment> void switchToFragment(Class<T> fragmentClass, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment existing  = fm.findFragmentByTag(tag);
        if (existing != null && existing.isVisible()) return; // already showing

        try {
            Fragment target = (existing != null) ? existing : fragmentClass.newInstance();
            showFragment(target, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFragment(Fragment fragment, String tag) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.fragment_container, fragment, tag);
        tx.commit();
    }
}
