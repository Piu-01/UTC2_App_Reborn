package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;

public class SupportActivity extends AppCompatActivity {

    EditText name, lop, phone, message;
    Button send;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        name = findViewById(R.id.edtName);
        lop = findViewById(R.id.edtClass);
        phone = findViewById(R.id.edtPhone);
        message = findViewById(R.id.edtMessage);

        send = findViewById(R.id.btnSend);
        back = findViewById(R.id.btnBack);

        back.setOnClickListener(v -> finish());

        send.setOnClickListener(v -> validateAndSend());
    }

    private void validateAndSend() {
        String userName = name.getText().toString().trim();
        String userClass = lop.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();
        String userMessage = message.getText().toString().trim();

        if (userName.isEmpty()) {
            name.setError("Chưa nhập họ tên");
            name.requestFocus();
            return;
        }

        if (userClass.isEmpty()) {
            lop.setError("Chưa nhập lớp");
            lop.requestFocus();
            return;
        }

        if (userMessage.isEmpty()) {
            message.setError("Chưa nhập nội dung hỗ trợ");
            message.requestFocus();
            return;
        }

        sendEmail(userName, userClass, userPhone, userMessage);
    }

    private void sendEmail(String name, String lop, String phone, String message) {
        String content = "Họ tên: " + name +
                "\nLớp: " + lop +
                "\nSĐT: " + (phone.isEmpty() ? "Không cung cấp" : phone) +
                "\n\nNội dung:\n" + message;

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // chỉ ứng dụng email
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hinhvinhphat@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Hỗ trợ UTC2 App - từ " + name);
        intent.putExtra(Intent.EXTRA_TEXT, content);

        try {
            startActivity(Intent.createChooser(intent, "Chọn ứng dụng Email"));
        } catch (Exception e) {
            Toast.makeText(this, "Không tìm thấy ứng dụng Email", Toast.LENGTH_LONG).show();
        }
    }
}