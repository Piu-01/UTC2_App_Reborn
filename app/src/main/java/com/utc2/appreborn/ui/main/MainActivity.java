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
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Đã vào MainActivity", Toast.LENGTH_LONG).show();

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

//        vd như ở class quản ly trang đó public class SettingFragment extends Fragment {
//            public SettingFragment() {
//                super(R.layout.fragment_setting);
//            }
//        }
        // 🔹 B1: Tạo biến Fragment để chứa màn hình cần chuyển
        Fragment fragment = null;

        // 🔹 B2: Dựa vào ID nhận được từ LiquidBar để chọn màn hình tương ứng
        // 👉 Lưu ý: ID này phải trùng với ID trong file menu (bottom_nav_menu.xml)
        if (id == R.id.nav_home) {
            //fragment = new HomeFragment();
        } else if (id == R.id.nav_schedule) {
          //  fragment = new ScheduleFragment();
        } else if (id == R.id.nav_register) {
          //  fragment = new RegisterFragment();
        } else if (id == R.id.nav_result) {
          //  fragment = new ResultFragment();
        } else if (id == R.id.nav_profile) {
           // fragment = new ProfileFragment();
        } else {
           // fragment = new HomeFragment(); // fallback
        }

    }
}