package com.utc2.appreborn.ui.tuition;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import java.util.ArrayList;
import java.util.List;

public class SubjectTuitionActivity extends AppCompatActivity {

    private RecyclerView rvSubjectTuition;
    private SubjectTuitionAdapter adapter;
    private List<SubjectTuition> subjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_tuition);

        // Nút Back
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Thiết lập RecyclerView
        rvSubjectTuition = findViewById(R.id.rvSubjectTuition);
        rvSubjectTuition.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo dữ liệu (Sau này sẽ thay bằng gọi từ SQL)
        loadData();

        // Gán adapter
        adapter = new SubjectTuitionAdapter(subjectList);
        rvSubjectTuition.setAdapter(adapter);
    }

    private void loadData() {
        subjectList = new ArrayList<>();
        // Dữ liệu mẫu để test giao diện
        subjectList.add(new SubjectTuition("Lập trình Android", "3 tín chỉ - Học kỳ II 2025-2026", "1.250.000 VND", "Đã đóng"));
        subjectList.add(new SubjectTuition("Cấu trúc dữ liệu", "4 tín chỉ - Học kỳ II 2025-2026", "1.600.000 VND", "Chưa đóng"));
        subjectList.add(new SubjectTuition("Anh văn chuyên ngành", "2 tín chỉ - Học kỳ II 2025-2026", "850.000 VND", "Đã đóng"));
    }
}