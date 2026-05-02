package com.utc2.appreborn.ui.tuition.Dorm;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.tuition.adapter.DormAdapter;
import com.utc2.appreborn.ui.tuition.model.DormTuition;
import com.utc2.appreborn.utils.NetworkUtils;
import java.util.ArrayList;
import java.util.List;

public class DormitoryTuitionActivity extends AppCompatActivity {

    private RecyclerView rvDormTuition;
    private List<DormTuition> dormList;
    private Button btnPayDorm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dormitory_tuition);

        try {
            initViews();
            loadDormData();
            setupRecyclerView();
        } catch (Exception e) {
            Log.e("DormTuition", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        rvDormTuition = findViewById(R.id.rvDormTuition);
        btnPayDorm = findViewById(R.id.btnPayDorm);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnPayDorm.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Chức năng thanh toán KTX đang được bảo trì!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cần kết nối mạng để tạo mã QR thanh toán!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDormData() {
        dormList = new ArrayList<>();
        dormList.add(new DormTuition("Phòng 402 - Dãy B", "Tháng 03/2026 - Điện nước", 650000, 0));
        dormList.add(new DormTuition("Phòng 402 - Dãy B", "Tháng 02/2026 - Điện nước", 720000, 0));
        dormList.add(new DormTuition("Phòng 402 - Dãy B", "Học kỳ 2 - Tiền phòng", 1500000, 1));
    }

    private void setupRecyclerView() {
        rvDormTuition.setLayoutManager(new LinearLayoutManager(this));
        DormAdapter adapter = new DormAdapter(dormList);
        rvDormTuition.setAdapter(adapter);
    }
}