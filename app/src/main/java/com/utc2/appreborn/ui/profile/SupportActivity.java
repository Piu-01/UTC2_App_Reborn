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

public class SupportActivity extends AppCompatActivity {

    AutoCompleteTextView dropLoai;
    EditText edtContent;
    RatingBar ratingBar;
    Button btnSend;
    ImageButton btnBack;

    String[] data = {"Lỗi", "Góp ý"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        // Ánh xạ view
        dropLoai = findViewById(R.id.dropLoai);
        edtContent = findViewById(R.id.edtContent);
        ratingBar = findViewById(R.id.ratingBar);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);

        // Dropdown (Material)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                data
        );

        dropLoai.setAdapter(adapter);

        // 👉 chọn item thì tự fill vào ô
        dropLoai.setOnItemClickListener((parent, view, position, id) -> {
            dropLoai.setText(data[position], false);
        });

        // Back
        btnBack.setOnClickListener(v -> finish());

        // Send
        btnSend.setOnClickListener(v -> validateAndSend());
    }

    private void validateAndSend() {
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
}