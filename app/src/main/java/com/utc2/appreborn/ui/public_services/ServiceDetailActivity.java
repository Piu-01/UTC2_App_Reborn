package com.utc2.appreborn.ui.public_services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.public_services.CardReissueService.CardReissueService;
import com.utc2.appreborn.ui.public_services.LoanSupportService.LoanSupportService;
import com.utc2.appreborn.ui.public_services.StudentConfirmationService.StudentConfirmationService;
import com.utc2.appreborn.ui.public_services.TranscriptService.TranscriptService;

public class ServiceDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvStatus, tvTime;
    private LinearLayout layoutDynamicContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        initViews();

        BaseService service = (BaseService) getIntent().getSerializableExtra("SERVICE_DATA");
        if (service != null) {
            populateData(service);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvStatus = findViewById(R.id.tvStatusBadge);
        tvTime = findViewById(R.id.tvTime);
        layoutDynamicContent = findViewById(R.id.layoutDynamicContent);
    }

    private void populateData(BaseService service) {
        tvTitle.setText(service.getTitle());
        tvTime.setText(getString(R.string.date_placeholder, service.getDate()));

        // Thiết lập Badge trạng thái
        if (service.getStatus() == 1) {
            tvStatus.setText(R.string.status_approved);
            tvStatus.setBackgroundResource(R.drawable.bg_status_done);
        } else {
            tvStatus.setText(R.string.status_pending);
            tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
        }

        layoutDynamicContent.removeAllViews();

        // 1. Dịch vụ Cấp lại thẻ
        if (service instanceof CardReissueService) {
            CardReissueService s = (CardReissueService) service;
            addInfoRow(getString(R.string.name_title), s.getStudentName());
            addInfoRow(getString(R.string.id_title), s.getStudentId());
            addInfoRow(getString(R.string.class_title), s.getClassName());
            addInfoRow("Lý do", s.getDescription());
        }
        // 2. Dịch vụ Hỗ trợ vay vốn
        else if (service instanceof LoanSupportService) {
            LoanSupportService s = (LoanSupportService) service;
            addInfoRow(getString(R.string.loan_amount), s.getLoanAmount());
            addInfoRow(getString(R.string.contact_number_title), s.getPhoneNumber());
            addInfoRow("Ghi chú", s.getDescription());
        }
        // 3. Dịch vụ Đăng ký bảng điểm (Mới)
        else if (service instanceof TranscriptService) {
            TranscriptService s = (TranscriptService) service;
            addInfoRow(getString(R.string.name_title), s.getStudentName());
            addInfoRow(getString(R.string.id_title), s.getStudentId());
            addInfoRow(getString(R.string.class_title), s.getClassName());
            addInfoRow(getString(R.string.transcript_academic_year), s.getAcademicYear());
            addInfoRow(getString(R.string.transcript_semester), s.getSemester());
            addInfoRow(getString(R.string.transcript_quantity), s.getQuantity());
        }
        // 4. Dịch vụ Xác nhận sinh viên (Mới)
        else if (service instanceof StudentConfirmationService) {
            StudentConfirmationService s = (StudentConfirmationService) service;
            addInfoRow(getString(R.string.name_title), s.getStudentName());
            addInfoRow(getString(R.string.id_title), s.getStudentId());
            addInfoRow(getString(R.string.class_title), s.getClassName());
            addInfoRow("Lý do xác nhận", s.getDescription());
        }
    }

    private void addInfoRow(String label, String value) {
        View rowView = LayoutInflater.from(this).inflate(R.layout.item_detail_info_row, layoutDynamicContent, false);
        TextView tvLabel = rowView.findViewById(R.id.tvLabel);
        TextView tvValue = rowView.findViewById(R.id.tvValue);

        tvLabel.setText(label);
        tvValue.setText(value != null && !value.isEmpty() ? value : "---");

        layoutDynamicContent.addView(rowView);
    }
}