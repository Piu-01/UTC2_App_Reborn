package com.utc2.appreborn.ui.tuition;

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
        invoiceList.add(new Invoice("Mã HD: UTC2_2026_001", "Ngày thanh toán: 10/04/2026", "2.500.000 VND"));
        invoiceList.add(new Invoice("Mã HD: UTC2_2026_002", "Ngày thanh toán: 15/03/2026", "1.250.000 VND"));
        invoiceList.add(new Invoice("Mã HD: UTC2_2026_003", "Ngày thanh toán: 05/02/2026", "650.000 VND"));
    }
}