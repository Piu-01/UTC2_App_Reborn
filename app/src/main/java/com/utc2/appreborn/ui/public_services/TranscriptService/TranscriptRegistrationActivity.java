package com.utc2.appreborn.ui.public_services.TranscriptService;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView; // Đổi Button thành TextView
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.utc2.appreborn.R;

public class TranscriptRegistrationActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView txtName, txtMSSV, txtClass;
    private AutoCompleteTextView dropAcademicYear, dropSemester;
    private EditText edtQuantity, edtNote;

    // SỬA: Đổi từ Button thành TextView để khớp với phong cách thiết kế XML của bạn
    private TextView btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript_registration);

        initViews();
        setupData();
        setupDropdowns();
        setupEvents();
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

        // Khởi tạo là TextView để tránh lỗi java.lang.ClassCastException
        btnConfirm = findViewById(R.id.btnConfirm);
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
            String academicYear = dropAcademicYear.getText().toString();
            String semester = dropSemester.getText().toString();
            String quantity = edtQuantity.getText().toString();
            String note = edtNote.getText().toString().trim();

            // Kiểm tra các trường bắt buộc (Năm, Kỳ, Số lượng)
            if (academicYear.isEmpty() || semester.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xử lý logic: Ghi chú (Lý do) không bắt buộc
            // Nếu note trống, ta để nội dung mô tả mặc định
            String finalDescription = "Số lượng: " + quantity + " bản - " + semester;
            if (!note.isEmpty()) {
                finalDescription += " (Ghi chú: " + note + ")";
            }

            TranscriptService newRequest = new TranscriptService(
                    getString(R.string.transcript_registration_title), // Dùng string resource cho tiêu đề
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

            // Lưu ý: TranscriptService cần có thêm trường description hoặc note tùy vào Model của bạn
            // Ở đây mình tận dụng trường description của BaseService để lưu thông tin tóm tắt

            Toast.makeText(this, "Đăng ký bảng điểm thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}