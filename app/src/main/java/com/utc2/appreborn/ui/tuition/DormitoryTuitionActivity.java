package com.utc2.appreborn.ui.tuition;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import java.util.ArrayList;
import java.util.List;

public class DormitoryTuitionActivity extends AppCompatActivity {

    private RecyclerView rvDormTuition;
    private DormAdapter adapter;
    private List<DormTuition> dormList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dormitory_tuition);

        // Nút Back
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Thiết lập RecyclerView
        rvDormTuition = findViewById(R.id.rvDormTuition);
        rvDormTuition.setLayoutManager(new LinearLayoutManager(this));

        // Load dữ liệu mẫu (Sau này thay bằng SQL query)
        loadDormData();

        // Gán Adapter
        adapter = new DormAdapter(dormList);
        rvDormTuition.setAdapter(adapter);
    }

    private void loadDormData() {
        dormList = new ArrayList<>();
        dormList.add(new DormTuition("Phòng 402 - Dãy B", "Tháng 03/2026 - Tiền phòng + Điện nước", "650.000 VND"));
        dormList.add(new DormTuition("Phòng 402 - Dãy B", "Tháng 02/2026 - Tiền phòng + Điện nước", "720.000 VND"));
        dormList.add(new DormTuition("Phòng 402 - Dãy B", "Tháng 01/2026 - Tiền phòng + Điện nước", "680.000 VND"));
    }
}
