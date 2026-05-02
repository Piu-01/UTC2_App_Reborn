package com.utc2.appreborn.ui.Info;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.utc2.appreborn.R;
import com.utc2.appreborn.databinding.FragmentInfoBinding;
import com.utc2.appreborn.ui.Info.adapter.InfoAdapter;
import com.utc2.appreborn.ui.Info.model.InfoItem;
import com.utc2.appreborn.ui.login.LoginActivity;
import com.utc2.appreborn.ui.profile.SupportActivity;
import com.utc2.appreborn.ui.profile.TrainingProgram.TrainingProgramActivity;
import com.utc2.appreborn.utils.NetworkUtils;

public class InfoFragment extends Fragment {

    private FragmentInfoBinding binding;
    private NetworkUtils networkUtils;
    private InfoAdapter infoAdapter;
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo Google Sign-In Client thay vì FirebaseAuth
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        // Kiểm tra phiên đăng nhập bằng Google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (account == null) {
            navigateToLogin();
            return;
        }

        setupRecyclerView();
        setupNetworkMonitoring();
        setupClickListeners();
        fetchStudentDataFromSQL();
    }

    private void setupRecyclerView() {
        infoAdapter = new InfoAdapter();
        binding.rvStudentDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStudentDetails.setAdapter(infoAdapter);
        binding.rvStudentDetails.setNestedScrollingEnabled(false);
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(requireContext(), new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                Log.d("Network", "Online");
            }

            @Override
            public void onNetworkLost() {
                if (isAdded()) {
                    // Sử dụng string resource để tránh lỗi "String literal" và Typo
                    Toast.makeText(requireContext(), getString(R.string.error_no_network), Toast.LENGTH_SHORT).show();
                }
            }
        });
        networkUtils.register();
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        binding.btnSupport.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SupportActivity.class)));

        binding.btnTrainingProgram.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), TrainingProgramActivity.class)));

        binding.btnLogout.setOnClickListener(v -> performLogout());
    }

    private void performLogout() {
        // Đăng xuất khỏi Google và điều hướng về Login
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> navigateToLogin());
    }

    private void navigateToLogin() {
        if (!isAdded()) return;
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void fetchStudentDataFromSQL() {
        // Mock data cho InfoItem[cite: 5]
        InfoItem data = new InfoItem(
                "Xã gió đó, Tỉnh Đồng Thuận", "083385896945785884",
                "Đồng Thuận, Việt Nam", "Quận 9, TP. Hồ Chí Minh",
                "Ký túc xá khu B, ĐHQG", ""
        );
        updateUI(data);
    }

    private void updateUI(InfoItem info) {
        if (info == null || binding == null) return;

        if (infoAdapter != null) {
            infoAdapter.setStudentData(info);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy đăng ký để tránh rò rỉ bộ nhớ (Memory leak)[cite: 4]
        if (networkUtils != null) networkUtils.unregister();
        binding = null;
    }
}