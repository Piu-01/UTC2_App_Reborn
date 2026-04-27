package com.utc2.appreborn.ui.login;

public interface IAuthService {
    interface AuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    void login(String email, String password, AuthCallback callback);

    // RESET MK:
    void resetPassword(String email, AuthCallback callback);
    //ĐỔI MK:
    void changePassword(String newPassword, AuthCallback callback);
}