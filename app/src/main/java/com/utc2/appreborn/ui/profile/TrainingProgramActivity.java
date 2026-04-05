package com.utc2.appreborn.ui.profile;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;

import java.util.ArrayList;
import java.util.List;

public class TrainingProgramActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubjectAdapter adapter;
    private List<Subject> subjectList;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_program);

        // ===== INIT VIEW =====
        recyclerView = findViewById(R.id.recyclerSubject);
        btnBack = findViewById(R.id.btnBackProfile);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ===== DATA DEMO =====
        subjectList = new ArrayList<>();

        subjectList.add(new Subject("BS0.001.2","GIẢI TÍCH 1","2","7.80"));
        subjectList.add(new Subject("BS0.101.3","ĐẠI SỐ TUYẾN TÍNH","3","6"));
        subjectList.add(new Subject("ANHA1.4","TIẾNG ANH A1","4","Hiện chưa có"));

        adapter = new SubjectAdapter(subjectList);
        recyclerView.setAdapter(adapter);

        // ===== BACK BUTTON =====
        btnBack.setOnClickListener(v -> finish());
    }
}