package com.utc2.appreborn.ui.profile.TrainingProgram;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.utc2.appreborn.R;
import java.util.ArrayList;
import java.util.List;

public class TrainingProgramActivity extends AppCompatActivity {

    // Chỉ giữ lại các biến cần dùng ở nhiều hàm khác nhau (Global Fields)
    private RecyclerView recyclerView;
    private SubjectAdapter adapter;
    private List<Subject> fullList;
    private Chip chipSem1, chipSem2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_program);

        try {
            initViews();
            loadData();
            setupEvents();
        } catch (Exception e) {
            Log.e("TrainingProgram", "Lỗi khởi tạo Activity: " + e.getMessage());
        }
    }

    private void initViews() {
        // Ánh xạ các biến toàn cục
        recyclerView = findViewById(R.id.recyclerSubject);
        chipSem1 = findViewById(R.id.chipSem1);
        chipSem2 = findViewById(R.id.chipSem2);

        // Chuyển các biến không dùng lại thành biến cục bộ (Fix: Field can be converted to a local variable)
        ImageButton btnBack = findViewById(R.id.btnBackProfile);
        SearchView searchView = findViewById(R.id.searchView);
        AutoCompleteTextView dropYear = findViewById(R.id.dropYear);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Thiết lập Dropdown chọn năm
        String[] years = {"Năm 1", "Năm 2", "Năm 3", "Năm 4"};
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, years);
        dropYear.setAdapter(dropdownAdapter);

        // Gán sự kiện cho các biến cục bộ ngay tại đây
        btnBack.setOnClickListener(v -> finish());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchSubject(newText);
                return true;
            }
        });

        dropYear.setOnItemClickListener((parent, view, position, id) -> {
            // Logic lọc theo năm nếu bạn phát triển thêm
        });
    }

    private void loadData() {
        fullList = new ArrayList<>();
        try {
            // Chèn Header Kỳ 1
            fullList.add(new Subject("", "KỲ HỌC 1", "", "", 1, true));
            fullList.add(new Subject("BS0.001.2", "GIẢI TÍCH 1", "2", "7.80", 1, false));
            fullList.add(new Subject("BS0.101.3", "ĐẠI SỐ TUYẾN TÍNH", "3", "6.0", 1, false));
            fullList.add(new Subject("ANHA1.4", "TIẾNG ANH A1", "4", "Chưa có", 1, false));

            // Chèn Header Kỳ 2
            fullList.add(new Subject("", "KỲ HỌC 2", "", "", 2, true));
            fullList.add(new Subject("IT1.002.3", "LẬP TRÌNH C++", "3", "8.5", 2, false));
            fullList.add(new Subject("IT2.005.3", "CẤU TRÚC DỮ LIỆU", "3", "Chưa có", 2, false));
        } catch (Exception e) {
            Log.e("TrainingProgram", "Lỗi nạp dữ liệu: " + e.getMessage());
        } finally {
            // Đảm bảo adapter luôn được khởi tạo kể cả khi lỗi dữ liệu
            adapter = new SubjectAdapter(fullList);
            recyclerView.setAdapter(adapter);
        }
    }

    private void setupEvents() {
        // Sử dụng scrollToSemester để tìm đúng vị trí Header
        chipSem1.setOnClickListener(v -> scrollToSemester(1));
        chipSem2.setOnClickListener(v -> scrollToSemester(2));
    }

    private void scrollToSemester(int sem) {
        try {
            // Kiểm tra null an toàn cho LayoutManager (Fix: NullPointerException warning)
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager llm = (LinearLayoutManager) layoutManager;

                for (int i = 0; i < fullList.size(); i++) {
                    Subject item = fullList.get(i);
                    if (item.isHeader() && item.getSemester() == sem) {
                        // Cuộn item lên sát mép trên cùng của màn hình (offset = 0)
                        llm.scrollToPositionWithOffset(i, 0);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TrainingProgram", "Lỗi khi cuộn trang: " + e.getMessage());
        }
    }

    private void searchSubject(String query) {
        List<Subject> filteredList = new ArrayList<>();
        try {
            String input = (query != null) ? query.toLowerCase().trim() : "";
            for (Subject item : fullList) {
                // Luôn giữ lại Header để phân vùng hoặc giữ môn học khớp tên
                if (item.isHeader() || item.getName().toLowerCase().contains(input)) {
                    filteredList.add(item);
                }
            }
        } catch (Exception e) {
            Log.e("TrainingProgram", "Lỗi khi tìm kiếm: " + e.getMessage());
        } finally {
            if (adapter != null) {
                adapter.updateList(filteredList);
            }
        }
    }
}