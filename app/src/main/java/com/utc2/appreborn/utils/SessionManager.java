package com.utc2.appreborn.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "AppRebornSession";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_LOGIN_TYPE = "login_type"; // "google" hoặc "manual"
    private static final String KEY_STUDENT_ID = "student_id";

    private static SessionManager instance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    // Khởi tạo Singleton để dùng chung một bộ nhớ ở mọi nơi
    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    // Lưu phiên đăng nhập khi thành công
    public void createLoginSession(String token, String type, String studentId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_LOGIN_TYPE, type);
        editor.putString(KEY_STUDENT_ID, studentId);
        editor.apply();
    }

    // Lấy Token để gắn vào các request Retrofit sau này
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // Kiểm tra xem sinh viên đã đăng nhập chưa để vào thẳng màn hình Home
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getLoginType() {
        return sharedPreferences.getString(KEY_LOGIN_TYPE, "");
    }

    // Đăng xuất - Xóa sạch dữ liệu phiên
    public void logout() {
        editor.clear();
        editor.apply();
    }
}