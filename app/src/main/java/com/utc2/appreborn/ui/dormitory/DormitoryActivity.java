package com.utc2.appreborn.ui.dormitory;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;

public class DormitoryActivity extends AppCompatActivity {

    LinearLayout listContainer, layoutSelected;
    TextView tabDangKy, tabTraPhong;
    TextView btnToa, btnGia, btnLoai, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dormitory);

        // ===== ÁNH XẠ =====
        listContainer = findViewById(R.id.listContainer);
        layoutSelected = findViewById(R.id.layoutSelected);

        tabDangKy = findViewById(R.id.tabDangKy);
        tabTraPhong = findViewById(R.id.tabTraPhong);

        btnToa = findViewById(R.id.btnToa);
        btnGia = findViewById(R.id.btnGia);
        btnLoai = findViewById(R.id.btnLoai);

        btnBack = findViewById(R.id.btnBack);

        // ===== BACK =====
        btnBack.setOnClickListener(v -> finish());

        // ===== MENU FILTER =====
        btnToa.setOnClickListener(v ->
                showMenu(btnToa, new String[]{"Tòa A", "Tòa B", "Tòa C"}));

        btnGia.setOnClickListener(v ->
                showMenu(btnGia, new String[]{"300k", "500k", "700k"}));

        btnLoai.setOnClickListener(v ->
                showMenu(btnLoai, new String[]{"Nam", "Nữ"}));

        // ===== TAB =====
        tabDangKy.setOnClickListener(v -> {
            tabDangKy.setBackgroundResource(R.drawable.bg_tab_selected);
            tabDangKy.setTextColor(Color.WHITE);

            tabTraPhong.setBackgroundColor(Color.TRANSPARENT);
            tabTraPhong.setTextColor(Color.BLACK);
        });

        tabTraPhong.setOnClickListener(v -> {
            tabTraPhong.setBackgroundResource(R.drawable.bg_tab_selected);
            tabTraPhong.setTextColor(Color.WHITE);

            tabDangKy.setBackgroundColor(Color.TRANSPARENT);
            tabDangKy.setTextColor(Color.BLACK);
        });

        // ===== DATA ROOM =====
        addRoom("Phòng 201 - Tòa A", 4, 300000, true);
        addRoom("Phòng 202 - Tòa A", 6, 450000, true);
        addRoom("Phòng 203 - Tòa B", 5, 500000, false);
        addRoom("Phòng 204 - Tòa B", 3, 250000, true);
        addRoom("Phòng 205 - Tòa C", 8, 700000, false);
    }

    // ===== MENU =====
    private void showMenu(TextView view, String[] items) {
        PopupMenu menu = new PopupMenu(this, view);

        for (String i : items) {
            menu.getMenu().add(i);
        }

        menu.setOnMenuItemClickListener(item -> {
            view.setText(item.getTitle());
            return true;
        });

        menu.show();
    }

    // ===== ADD ROOM =====
    private void addRoom(String name, int people, int price, boolean available) {

        View view = getLayoutInflater().inflate(R.layout.item_room, listContainer, false);

        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtInfo = view.findViewById(R.id.txtInfo);
        TextView txtStatus = view.findViewById(R.id.txtStatus);
        Button btnRegister = view.findViewById(R.id.btnRegister);

        // SET DATA
        txtTitle.setText(name);
        txtInfo.setText("👥 " + people + "   💰 " + price + "đ/tháng");

        if (available) {
            txtStatus.setText("✔ Còn chỗ");
            txtStatus.setTextColor(Color.parseColor("#00C853"));

            btnRegister.setEnabled(true);
            btnRegister.setBackgroundResource(R.drawable.bg_button_black);

        } else {
            txtStatus.setText("✖ Hết chỗ");
            txtStatus.setTextColor(Color.RED);

            btnRegister.setEnabled(false);
            btnRegister.setBackgroundResource(R.drawable.bg_button_disabled);
        }

        // CLICK ĐĂNG KÝ
        btnRegister.setOnClickListener(v -> {
            layoutSelected.setVisibility(View.VISIBLE);

            TextView txtRoomInfo = findViewById(R.id.txtRoomInfo);
            TextView txtTotal = findViewById(R.id.txtTotal);

            if (txtRoomInfo != null) {
                txtRoomInfo.setText(name);
            }

            if (txtTotal != null) {
                txtTotal.setText("Tổng: " + (price * 8) + "đ");
            }
        });

        listContainer.addView(view);
    }
}