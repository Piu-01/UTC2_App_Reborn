package com.utc2.appreborn.ui.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.main.MainActivity;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText editMssv, editPassword;
    private Button loginBtn, googleLoginBtn;
    private GoogleSignInClient mGoogleSignInClient;
    private SessionManager sessionManager;
    private Dialog loadingDialog;

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                setLoading(false);
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = SessionManager.getInstance(this);

        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        initViews();
        setupGoogleSignIn();
        createLoadingDialog();
    }

    private void initViews() {
        editMssv = findViewById(R.id.editMssv);
        editPassword = findViewById(R.id.editPassword);
        loginBtn = findViewById(R.id.loginBtn);
        googleLoginBtn = findViewById(R.id.googleLoginBtn);
        Button skipBtn = findViewById(R.id.skipBtn);

        loginBtn.setOnClickListener(v -> performManualLogin());
        googleLoginBtn.setOnClickListener(v -> performGoogleSignIn());
        skipBtn.setOnClickListener(v -> performSkipLogin());
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void performManualLogin() {
        String mssv = editMssv.getText().toString().trim();
        String pass = editPassword.getText().toString().trim();

        if (mssv.isEmpty() || pass.isEmpty()) {
            showToast(getString(R.string.error_fill_all));
            return;
        }

        setLoading(true);

        // Kiểm tra tài khoản hardcoded
        if (mssv.equals(getString(R.string.default_mssv)) && pass.equals("123456")) {
            sessionManager.createLoginSession("MANUAL_TOKEN", "mssv", mssv);
            showToast(getString(R.string.welcome_user) + " " + getString(R.string.default_name));
            setLoading(false);
            navigateToMain();
        } else {
            setLoading(false);
            showToast(getString(R.string.wrong_email_or_pass));
        }
    }

    private void performSkipLogin() {
        sessionManager.createLoginSession("GUEST_TOKEN", "role", "guest");
        showToast(getString(R.string.skip_login) + "...");
        navigateToMain();
    }

    private void performGoogleSignIn() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showToast(getString(R.string.error_connect_network));
            return;
        }
        setLoading(true);
        googleSignInLauncher.launch(mGoogleSignInClient.getSignInIntent());
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                sessionManager.createLoginSession(account.getIdToken(), "google", account.getEmail());
                showToast(getString(R.string.welcome_user) + " " + account.getDisplayName());
                navigateToMain();
            }
        } catch (ApiException e) {
            showToast(getString(R.string.error_login_failed));
        }
    }

    private void createLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.custom_loading);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.show();
            }
        } else {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}