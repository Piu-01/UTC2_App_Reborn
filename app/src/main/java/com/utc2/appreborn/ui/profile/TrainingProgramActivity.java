package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;

import java.util.ArrayList;
import java.util.List;

public class TrainingProgramActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SubjectAdapter adapter;
    List<Subject> subjectList;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_program);

        recyclerView = findViewById(R.id.recyclerSubject);
        btnBack = findViewById(R.id.btnBackProfile);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        subjectList = new ArrayList<>();

        // DATA DEMO
        subjectList.add(new Subject("BS0.001.2","GIẢI TÍCH 1","2","7.80"));
        subjectList.add(new Subject("BS0.101.3","ĐẠI SỐ TUYẾN TÍNH","3","6"));
        subjectList.add(new Subject("ANHA1.4","TIẾNG ANH A1","4","hiện chưa có"));

        adapter = new SubjectAdapter(subjectList);
        recyclerView.setAdapter(adapter);

        // 👉 NÚT QUAY VỀ PROFILE
        btnBack.setOnClickListener(v -> finish());
    }
}