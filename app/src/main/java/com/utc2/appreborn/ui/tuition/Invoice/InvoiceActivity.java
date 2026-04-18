package com.utc2.appreborn.ui.tuition.Invoice;

import com.utc2.appreborn.ui.tuition.Dorm.DormTuition;
import com.utc2.appreborn.ui.tuition.Subject.SubjectTuition;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import java.util.ArrayList;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {

    private RecyclerView rvInvoices;
    private InvoiceAdapter adapter;
    private List<Invoice> invoiceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // Nút Back
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Thiết lập RecyclerView
        rvInvoices = findViewById(R.id.rvInvoices);
        rvInvoices.setLayoutManager(new LinearLayoutManager(this));

        // Load dữ liệu mẫu (Sau này thay bằng SQL query)
        loadInvoiceData();

        // Gán Adapter
        adapter = new InvoiceAdapter(invoiceList);
        rvInvoices.setAdapter(adapter);
    }

    private void loadInvoiceData() {
        invoiceList = new ArrayList<>();

        // Tạo các đối tượng học phí/lệ phí cụ thể (Lớp con)
        // Cấu trúc: SubjectTuition(id, name, details, amount, status)
        SubjectTuition monHoc = new SubjectTuition(1, "Lập trình Android", "Học kỳ 2", 2500000, 1);
        DormTuition tienPhong = new DormTuition("Phòng 403", "Tháng 03/2026", 1250000, 1);
        SubjectTuition monHoc2 = new SubjectTuition(2, "Cấu trúc dữ liệu", "Học kỳ 1", 650000, 1);

        // Thêm vào danh sách hóa đơn
        // Bây giờ Invoice nhận (Mã HD, Ngày, Đối tượng Tuition)
        invoiceList.add(new Invoice("UTC2_2026_001", "10/04/2026", monHoc));
        invoiceList.add(new Invoice("UTC2_2026_002", "15/03/2026", tienPhong));
        invoiceList.add(new Invoice("UTC2_2026_003", "05/02/2026", monHoc2));
    }
}