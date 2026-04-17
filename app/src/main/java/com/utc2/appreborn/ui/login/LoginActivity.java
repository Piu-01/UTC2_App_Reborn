package com.utc2.appreborn.ui.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button loginBtn, skipBtn;
    private TextView txtForgot, txtTerms;

    private IAuthService authService;
    private Dialog loadingDialog; // Đã đổi sang Dialog thuần

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        createLoadingDialog();
        setupSpannableTerms();

        txtForgot.setOnClickListener(v -> navigateTo(ForgotPasswordActivity.class));
        loginBtn.setOnClickListener(v -> validateAndLogin());
        skipBtn.setOnClickListener(v -> performLogin("test123@gmail.com", "123456", true));
    }

    private void initViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        skipBtn = findViewById(R.id.skipBtn);
        txtForgot = findViewById(R.id.txtForgot);
        txtTerms = findViewById(R.id.txtTerms);

        authService = new FirebaseAuthService();
    }

    private void createLoadingDialog() {
        // Khởi tạo Dialog thuần
        loadingDialog = new Dialog(this);

        // Loại bỏ tiêu đề mặc định của hệ thống
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Nạp layout hình vuông của bạn
        loadingDialog.setContentView(R.layout.custom_loading);

        // Không cho phép đóng khi đang load
        loadingDialog.setCancelable(false);

        if (loadingDialog.getWindow() != null) {
            // FIX: Xóa bỏ khung nền đen mặc định của hệ thống
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Giữ hiệu ứng mờ nền phía sau (dim), nếu muốn xóa hẳn thì dùng FLAG_DIM_BEHIND
            loadingDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.show();

                // FIX: Ép Window co lại theo kích thước hình vuông (ví dụ 140dp) trong XML
                // Nếu không có dòng này, Android sẽ tự kéo dài thành hình chữ nhật
                if (loadingDialog.getWindow() != null) {
                    loadingDialog.getWindow().setLayout(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT
                    );
                }
            }
        } else {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    private void validateAndLogin() {
        if (!isNetworkAvailable()) {
            showToast("Không có kết nối mạng!");
            return;
        }

        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        if (userEmail.isEmpty()) {
            email.setError("Vui lòng nhập email!");
            email.requestFocus();
            return;
        }

        if (userPass.isEmpty()) {
            password.setError("Vui lòng nhập mật khẩu!");
            password.requestFocus();
            return;
        }

        performLogin(userEmail, userPass, false);
    }

    private void performLogin(String emailStr, String passStr, boolean isSkip) {
        setLoading(true);

        authService.login(emailStr, passStr, new IAuthService.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                setLoading(false);
                showToast(isSkip ? "Bỏ qua thành công" : message);
                navigateTo(MainActivity.class);
                finish();
            }

            @Override
            public void onError(String error) {
                setLoading(false);
                showToast(isSkip ? "Lỗi bỏ qua" : error);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        android.net.Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    private void setupSpannableTerms() {
        String text = "By continuing you agree to our Terms of Service and Privacy Policy";
        SpannableString ss = new SpannableString(text);

        ClickableSpan terms = new ClickableSpan() {
            @Override public void onClick(@NonNull View w) { navigateTo(TermsActivity.class); }
        };
        ClickableSpan privacy = new ClickableSpan() {
            @Override public void onClick(@NonNull View w) { navigateTo(PrivacyPolicyActivity.class); }
        };

        int startT = text.indexOf("Terms of Service");
        ss.setSpan(terms, startT, startT + 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int startP = text.indexOf("Privacy Policy");
        ss.setSpan(privacy, startP, startP + 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtTerms.setText(ss);
        txtTerms.setMovementMethod(LinkMovementMethod.getInstance());
        txtTerms.setHighlightColor(Color.TRANSPARENT);
    }

    private void navigateTo(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}