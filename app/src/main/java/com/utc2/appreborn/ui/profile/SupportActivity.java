package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;
import com.utc2.appreborn.utils.NetworkUtils; // Import Utils

public class SupportActivity extends AppCompatActivity {

    private AutoCompleteTextView dropLoai;
    private EditText edtContent;
    private RatingBar ratingBar;
    private Button btnSend;
    private ImageButton btnBack;

    private final String[] data = {"Lỗi", "Góp ý"};
    private NetworkUtils networkUtils; // Thêm NetworkUtils

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        initViews();
        setupDropdown();
        setupNetworkMonitoring(); // Khởi tạo lắng nghe mạng

        // Sự kiện Back
        btnBack.setOnClickListener(v -> finish());

        // Sự kiện Gửi
        btnSend.setOnClickListener(v -> validateAndSend());
    }

    private void initViews() {
        dropLoai = findViewById(R.id.dropLoai);
        edtContent = findViewById(R.id.edtContent);
        ratingBar = findViewById(R.id.ratingBar);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                data
        );
        dropLoai.setAdapter(adapter);

        // Chọn item thì tự fill vào ô
        dropLoai.setOnItemClickListener((parent, view, position, id) -> {
            dropLoai.setText(data[position], false);
        });
    }

    // Thiết lập lắng nghe mạng liên tục
    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                btnSend.setEnabled(true);
                btnSend.setAlpha(1.0f);
            }

            @Override
            public void onNetworkLost() {
                Toast.makeText(SupportActivity.this, "Không có kết nối mạng!", Toast.LENGTH_SHORT).show();
                btnSend.setEnabled(false);
                btnSend.setAlpha(0.5f);
            }
        });
        networkUtils.register();
    }

    private void validateAndSend() {
        // Kiểm tra mạng tức thời trước khi gửi
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Vui lòng kết nối mạng để gửi hỗ trợ", Toast.LENGTH_SHORT).show();
            return;
        }

        String loai = dropLoai.getText().toString().trim();
        String content = edtContent.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (loai.isEmpty()) {
            dropLoai.setError("Chưa chọn loại");
            dropLoai.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            edtContent.setError("Chưa nhập nội dung");
            edtContent.requestFocus();
            return;
        }

        sendEmail(loai, content, rating);
    }

    private void sendEmail(String loai, String content, float rating) {
        String danhGia;
        if (rating == 1) danhGia = "Rất kém";
        else if (rating == 2) danhGia = "Kém";
        else if (rating == 3) danhGia = "Trung bình";
        else if (rating == 4) danhGia = "Tốt";
        else if (rating == 5) danhGia = "Rất tốt";
        else danhGia = "Chưa đánh giá";

        String message = "Loại: " + loai +
                "\nMức độ hài lòng: " + danhGia + " (" + rating + "/5)" +
                "\n\nNội dung:\n" + content;

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hinhvinhphat@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "UTC2 Support - " + loai);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(intent, "Chọn ứng dụng Email"));
        } catch (Exception e) {
            Toast.makeText(this, "Không tìm thấy ứng dụng Email", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký lắng nghe mạng khi thoát activity
        if (networkUtils != null) {
            networkUtils.unregister();
        }
    }
}