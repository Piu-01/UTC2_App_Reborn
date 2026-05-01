package com.utc2.appreborn.ui.login;

public class FirebaseAuthService implements IAuthService {
    private com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();

    @Override
    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess("Success");
                    } else {
                        callback.onError("@string/wrong_email_or_pass");
                    }
                });
    }
    //FORGOT PASS
    @Override
    public void resetPassword(String email, AuthCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> callback.onSuccess("@string/check_email_for_pass"))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    //CHANGE PASS
    @Override
    public void changePassword(String newPassword, AuthCallback callback) {
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess("Cập nhật mật khẩu thành công!");
                        } else {
                            // Firebase thường bắt re-authenticate nếu user đã login quá lâu
                            callback.onError(task.getException() != null ?
                                    task.getException().getMessage() : "Lỗi không xác định");
                        }
                    });
        } else {
            callback.onError("Phiên đăng nhập đã hết hạn");
        }
    }
}