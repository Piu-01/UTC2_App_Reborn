package com.utc2.appreborn.ui.profile.TrainingProgram;

import android.os.Bundle;
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

    private RecyclerView recyclerView;
    private SubjectAdapter adapter;
    private List<Subject> fullList;
    private ImageButton btnBack;
    private SearchView searchView;
    private AutoCompleteTextView dropYear; // Đã đổi tên và kiểu dữ liệu
    private Chip chipSem1, chipSem2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_program);

        initViews();
        loadData();
        setupEvents();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerSubject);
        btnBack = findViewById(R.id.btnBackProfile);
        searchView = findViewById(R.id.searchView);
        dropYear = findViewById(R.id.dropYear); // Ánh xạ đúng ID dropYear trong XML
        chipSem1 = findViewById(R.id.chipSem1);
        chipSem2 = findViewById(R.id.chipSem2);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Thiết lập Adapter cho Material Dropdown
        String[] years = {"Năm 1", "Năm 2", "Năm 3", "Năm 4"};
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, years); // simple_list_item_1 đẹp hơn cho dropdown
        dropYear.setAdapter(dropdownAdapter);
    }

    private void loadData() {
        fullList = new ArrayList<>();
        fullList.add(new Subject("BS0.001.2", "GIẢI TÍCH 1", "2", "7.80"));
        fullList.add(new Subject("BS0.101.3", "ĐẠI SỐ TUYẾN TÍNH", "3", "6.0"));
        fullList.add(new Subject("ANHA1.4", "TIẾNG ANH A1", "4", "Chưa có"));
        fullList.add(new Subject("IT1.002.3", "LẬP TRÌNH C++", "3", "8.5"));
        fullList.add(new Subject("IT2.005.3", "CẤU TRÚC DỮ LIỆU", "3", "Chưa có"));

        adapter = new SubjectAdapter(fullList);
        recyclerView.setAdapter(adapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        // Lắng nghe chọn năm học từ Dropdown
        dropYear.setOnItemClickListener((parent, view, position, id) -> {
            String selectedYear = (String) parent.getItemAtPosition(position);
            // Bạn có thể viết thêm hàm filter theo năm ở đây
        });

        // Logic Tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchSubject(newText);
                return true;
            }
        });

        // Logic Cuộn nhanh tới các kỳ
        chipSem1.setOnClickListener(v -> recyclerView.smoothScrollToPosition(0));
        chipSem2.setOnClickListener(v -> {
            if (fullList.size() >= 3) recyclerView.smoothScrollToPosition(3);
        });
    }

    private void searchSubject(String query) {
        List<Subject> filteredList = new ArrayList<>();
        for (Subject item : fullList) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }
}