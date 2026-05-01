package com.utc2.appreborn.ui.public_services.TranscriptService;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.utc2.appreborn.R;
import com.utc2.appreborn.utils.NetworkUtils; // Import Utils

public class TranscriptRegistrationActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView txtName, txtMSSV, txtClass;
    private AutoCompleteTextView dropAcademicYear, dropSemester;
    private EditText edtQuantity, edtNote;
    private TextView btnConfirm;

    // Quản lý trạng thái mạng
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript_registration);

        try {
            initViews();
            setupData();
            setupDropdowns();
            setupNetworkMonitoring(); // Khởi tạo lắng nghe mạng
            setupEvents();
        } catch (Exception e) {
            Log.e("TranscriptReg", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        txtName = findViewById(R.id.txtName);
        txtMSSV = findViewById(R.id.txtMSSV);
        txtClass = findViewById(R.id.txtClass);
        dropAcademicYear = findViewById(R.id.dropAcademicYear);
        dropSemester = findViewById(R.id.dropSemester);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtNote = findViewById(R.id.edtNote);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                Log.d("Network", "Sẵn sàng đăng ký bảng điểm");
            }

            @Override
            public void onNetworkLost() {
                Toast.makeText(TranscriptRegistrationActivity.this,
                        "Mất kết nối mạng! Vui lòng kiểm tra lại để không bị gián đoạn đăng ký.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    private void setupData() {
        txtName.setText(getString(R.string.default_name));
        txtMSSV.setText(getString(R.string.default_mssv));
        txtClass.setText(getString(R.string.default_class));
    }

    private void setupDropdowns() {
        String[] academicYears = {"2023 - 2024", "2024 - 2025", "Tất cả các năm"};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, academicYears);
        dropAcademicYear.setAdapter(yearAdapter);

        String[] semesters = {"Học kỳ 1", "Học kỳ 2"};
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, semesters);
        dropSemester.setAdapter(semesterAdapter);

        dropAcademicYear.setOnClickListener(v -> dropAcademicYear.showDropDown());
        dropSemester.setOnClickListener(v -> dropSemester.showDropDown());
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            // CHỐT CHẶN 1: Kiểm tra mạng
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Không có mạng! Vui lòng kết nối để gửi yêu cầu.", Toast.LENGTH_SHORT).show();
                return;
            }

            String academicYear = dropAcademicYear.getText().toString();
            String semester = dropSemester.getText().toString();
            String quantity = edtQuantity.getText().toString();
            String note = edtNote.getText().toString().trim();

            // CHỐT CHẶN 2: Kiểm tra dữ liệu bắt buộc
            if (academicYear.isEmpty() || semester.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String finalDescription = "Số lượng: " + quantity + " bản - " + semester;
                if (!note.isEmpty()) {
                    finalDescription += " (Ghi chú: " + note + ")";
                }

                TranscriptService newRequest = new TranscriptService(
                        getString(R.string.transcript_registration_title),
                        finalDescription,
                        System.currentTimeMillis(),
                        0,
                        "TRANSCRIPT_REG",
                        txtName.getText().toString(),
                        txtMSSV.getText().toString(),
                        txtClass.getText().toString(),
                        academicYear,
                        semester,
                        quantity
                );

                // --- GỌI API GỬI LÊN SERVER TẠI ĐÂY ---
                // api.submitTranscriptRequest(newRequest);

                Toast.makeText(this, "Đăng ký bảng điểm thành công!", Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkUtils != null) {
            networkUtils.unregister();
        }
    }
}