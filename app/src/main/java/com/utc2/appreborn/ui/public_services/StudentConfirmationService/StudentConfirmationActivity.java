package com.utc2.appreborn.ui.public_services.StudentConfirmationService;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView; // Đảm bảo đã import TextView
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.utc2.appreborn.R;

public class StudentConfirmationActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView txtName, txtMSSV, txtClass;
    private EditText edtReason;

    private TextView btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_confirmation);

        initViews();
        setupData();
        setupEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        txtName = findViewById(R.id.txtName);
        txtMSSV = findViewById(R.id.txtMSSV);
        txtClass = findViewById(R.id.txtClass);
        edtReason = findViewById(R.id.edtReason);

        // Dòng này sẽ không còn bị crash vì kiểu dữ liệu đã khớp với XML
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void setupData() {
        txtName.setText(getString(R.string.default_name));
        txtMSSV.setText(getString(R.string.default_mssv));
        txtClass.setText(getString(R.string.default_class));
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            String reason = edtReason.getText().toString().trim();

            if (reason.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập lý do đăng ký", Toast.LENGTH_SHORT).show();
                return;
            }

            StudentConfirmationService newRequest = new StudentConfirmationService(
                    getString(R.string.student_confirmation_title),
                    reason,
                    System.currentTimeMillis(),
                    0,
                    "STUDENT_CONFIRM",
                    txtName.getText().toString(),
                    txtMSSV.getText().toString(),
                    txtClass.getText().toString()
            );

            Toast.makeText(this, "Đăng ký giấy xác nhận thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}