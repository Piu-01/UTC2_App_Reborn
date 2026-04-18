package com.utc2.appreborn.ui.dormitory;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.dormitory.adapter.RoomAdapter;
import com.utc2.appreborn.ui.dormitory.exception.DormitoryException;
import com.utc2.appreborn.ui.dormitory.model.DormitoryRegistration;
import com.utc2.appreborn.ui.dormitory.model.DormitoryRepository;
import com.utc2.appreborn.ui.dormitory.model.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Kí túc xá – đã refactor theo chuẩn OOP.
 *
 * [Chương 3 - OOP áp dụng]
 *  - Sử dụng Room, DormitoryItem, DormitoryRegistration, DormitoryRepository, RoomAdapter
 *  - Kế thừa / Override / Overload / Bao đóng đều nằm trong các lớp model và adapter
 *
 * [Chương 4 - Xử lý ngoại lệ]
 *  - try-catch-finally trong handleRegisterClick(), handleCancelClick(), applyFilter()
 *  - throws DormitoryException ở tầng Repository
 *
 * [Chương 5 - Collection]
 *  - List<Room>, Map<String,Room>, Map<String,DormitoryRegistration> đều dùng trong Repository
 */
public class DormitoryActivity extends AppCompatActivity {

    private static final String TAG = "DormitoryActivity";

    // ── Views ─────────────────────────────────────────────────────────────────
    private RecyclerView  rvRooms;
    private EditText      searchBox;
    private LinearLayout  layoutSelected;
    private TextView      txtRoomInfo, txtRoomSubInfo, txtTotal;
    private TextView      tabDangKy, tabTraPhong;
    private TextView      btnToa, btnGia, btnLoai, btnBack;
    private TextView      btnCancel;  // TextView thay Button – tránh Material override màu

    // ── OOP: tách logic vào Repository & Adapter ──────────────────────────────
    private DormitoryRepository repository;
    private RoomAdapter         roomAdapter;

    // ── Trạng thái filter ─────────────────────────────────────────────────────
    private String        filterBuilding = "";
    private int           filterMaxPrice = 0;
    private Room.RoomType filterRoomType = null;

    // ── ID đăng ký hiện tại (để hủy) ─────────────────────────────────────────
    private String currentRegId = null;

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dormitory);

        bindViews();

        repository = DormitoryRepository.getInstance();

        setupRecyclerView();
        setupTabListeners();
        setupFilterButtons();
        setupSearchBox();
        setupCancelButton();

        btnBack.setOnClickListener(v -> finish());
    }

    // ── Ánh xạ Views ──────────────────────────────────────────────────────────
    private void bindViews() {
        rvRooms        = findViewById(R.id.rvRooms);
        searchBox      = findViewById(R.id.searchBox);
        layoutSelected = findViewById(R.id.layoutSelected);
        txtRoomInfo    = findViewById(R.id.txtRoomInfo);
        txtRoomSubInfo = findViewById(R.id.txtRoomSubInfo);
        txtTotal       = findViewById(R.id.txtTotal);
        tabDangKy      = findViewById(R.id.tabDangKy);
        tabTraPhong    = findViewById(R.id.tabTraPhong);
        btnToa         = findViewById(R.id.btnToa);
        btnGia         = findViewById(R.id.btnGia);
        btnLoai        = findViewById(R.id.btnLoai);
        btnBack        = findViewById(R.id.btnBack);
        btnCancel      = findViewById(R.id.btnCancel);
    }

    // ── RecyclerView ──────────────────────────────────────────────────────────
    private void setupRecyclerView() {
        // Lấy danh sách từ Repository (Collection List<Room>)
        List<Room> rooms = repository.getAllRooms();

        // Tạo Adapter với lambda callback (giống pattern FeatureAdapter bên Home)
        roomAdapter = new RoomAdapter(rooms, this::handleRegisterClick);

        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        rvRooms.setNestedScrollingEnabled(false);
        rvRooms.setAdapter(roomAdapter);
    }

    // ── Đăng ký phòng ─────────────────────────────────────────────────────────
    /**
     * [Chương 4] try-catch-finally xử lý đăng ký phòng.
     *  - try   : gọi repository.registerRoom() có thể throws DormitoryException
     *  - catch DormitoryException : lỗi nghiệp vụ (hết chỗ, tháng sai,...)
     *  - catch Exception          : lỗi không mong đợi
     *  - finally                  : luôn log dù thành công hay thất bại
     */
    private void handleRegisterClick(Room room) {
        try {
            DormitoryRegistration reg = repository.registerRoom(room.getId(), 8);
            currentRegId = reg.getId();

            layoutSelected.setVisibility(View.VISIBLE);
            txtRoomInfo.setText(room.getName());
            if (txtRoomSubInfo != null) {
                txtRoomSubInfo.setText(room.getDisplayInfo());
            }
            txtTotal.setText("Tổng số tiền: "
                    + String.format("%,d", reg.getTotalPrice()) + "đ");

            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

        } catch (DormitoryException e) {
            // Lỗi nghiệp vụ có thể đoán trước
            Log.w(TAG, "DormitoryException: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            // Lỗi ngoài dự kiến
            Log.e(TAG, "Unexpected error khi đăng ký", e);
            Toast.makeText(this, "Lỗi không xác định, vui lòng thử lại.", Toast.LENGTH_SHORT).show();

        } finally {
            Log.d(TAG, "handleRegisterClick() done – roomId: " + room.getId());
        }
    }

    // ── Hủy đăng ký ──────────────────────────────────────────────────────────
    private void setupCancelButton() {
        if (btnCancel == null) return;
        btnCancel.setOnClickListener(v -> handleCancelClick());
    }

    private void handleCancelClick() {
        try {
            if (currentRegId != null) {
                repository.cancelRegistration(currentRegId);
                currentRegId = null;
            }
            layoutSelected.setVisibility(View.GONE);
            Toast.makeText(this, "Đã hủy đăng ký.", Toast.LENGTH_SHORT).show();

        } catch (DormitoryException e) {
            Log.w(TAG, "Cancel error: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

        } finally {
            Log.d(TAG, "handleCancelClick() done.");
        }
    }

    // ── Tab ───────────────────────────────────────────────────────────────────
    private void setupTabListeners() {
        tabDangKy.setOnClickListener(v -> {
            tabDangKy.setBackgroundResource(R.drawable.bg_tab_selected);
            tabDangKy.setTextColor(android.graphics.Color.WHITE);
            tabTraPhong.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            tabTraPhong.setTextColor(android.graphics.Color.BLACK);
        });

        tabTraPhong.setOnClickListener(v -> {
            tabTraPhong.setBackgroundResource(R.drawable.bg_tab_selected);
            tabTraPhong.setTextColor(android.graphics.Color.WHITE);
            tabDangKy.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            tabDangKy.setTextColor(android.graphics.Color.BLACK);
        });
    }

    // ── Filter buttons ────────────────────────────────────────────────────────
    private void setupFilterButtons() {

        btnToa.setOnClickListener(v ->
                showPopupMenu(btnToa,
                        new String[]{"Tất cả", "Tòa A", "Tòa B", "Tòa C"},
                        choice -> {
                            filterBuilding = choice.equals("Tất cả") ? "" : choice.replace("Tòa ", "");
                            btnToa.setText(choice + " ▾");
                            applyFilter();
                        }));

        btnGia.setOnClickListener(v ->
                showPopupMenu(btnGia,
                        new String[]{"Tất cả", "≤ 300,000đ", "≤ 500,000đ", "≤ 700,000đ"},
                        choice -> {
                            if      (choice.contains("300")) filterMaxPrice = 300000;
                            else if (choice.contains("500")) filterMaxPrice = 500000;
                            else if (choice.contains("700")) filterMaxPrice = 700000;
                            else                             filterMaxPrice = 0;
                            btnGia.setText(choice + " ▾");
                            applyFilter();
                        }));

        btnLoai.setOnClickListener(v ->
                showPopupMenu(btnLoai,
                        new String[]{"Tất cả", "Nam", "Nữ"},
                        choice -> {
                            if      (choice.equals("Nam")) filterRoomType = Room.RoomType.NAM;
                            else if (choice.equals("Nữ"))  filterRoomType = Room.RoomType.NU;
                            else                           filterRoomType = null;
                            btnLoai.setText(choice + " ▾");
                            applyFilter();
                        }));
    }

    /** Interface nội bộ cho PopupMenu callback. */
    private interface MenuCallback {
        void onSelected(String choice);
    }

    private void showPopupMenu(TextView anchor, String[] options, MenuCallback cb) {
        PopupMenu menu = new PopupMenu(this, anchor);
        for (String opt : options) menu.getMenu().add(opt);
        menu.setOnMenuItemClickListener(item -> {
            cb.onSelected(item.getTitle().toString());
            return true;
        });
        menu.show();
    }

    /**
     * [Chương 4] Áp dụng filter – try-catch-finally.
     */
    private void applyFilter() {
        try {
            List<Room> filtered = repository.filterRooms(
                    filterBuilding, filterMaxPrice, filterRoomType);
            roomAdapter.updateData(filtered);

        } catch (DormitoryException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

        } finally {
            Log.d(TAG, "applyFilter() done.");
        }
    }

    // ── Tìm kiếm ─────────────────────────────────────────────────────────────
    private void setupSearchBox() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String query = s.toString().trim().toLowerCase();
                    List<Room> all      = repository.getAllRooms();
                    List<Room> filtered = new ArrayList<>();
                    for (Room room : all) {
                        if (room.getName().toLowerCase().contains(query)) {
                            filtered.add(room);
                        }
                    }
                    roomAdapter.updateData(filtered);
                } catch (Exception e) {
                    Log.e(TAG, "Search error", e);
                }
            }
        });
    }
}