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

import com.google.firebase.auth.FirebaseAuth;
import com.utc2.appreborn.databinding.FragmentInfoBinding;
import com.utc2.appreborn.ui.Info.adapter.InfoAdapter;
import com.utc2.appreborn.ui.Info.model.InfoItem;
import com.utc2.appreborn.ui.login.LoginActivity;
import com.utc2.appreborn.ui.profile.SupportActivity;
import com.utc2.appreborn.ui.profile.TrainingProgram.TrainingProgramActivity;
import com.utc2.appreborn.utils.NetworkUtils;

public class InfoFragment extends Fragment {

    private FragmentInfoBinding binding;
    private FirebaseAuth mAuth;
    private NetworkUtils networkUtils;
    private InfoAdapter infoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
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
                    Toast.makeText(requireContext(), "Bạn đang ngoại tuyến", Toast.LENGTH_SHORT).show();
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

        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            navigateToLogin();
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void fetchStudentDataFromSQL() {
        // Mock data cho InfoItem
        InfoItem data = new InfoItem(
                "Xã gió đó, Tỉnh Đồng Thuận", "083385896945785884",
                "Đồng Thuận, Việt Nam", "Quận 9, TP. Hồ Chí Minh",
                "Ký túc xá khu B, ĐHQG", ""
        );
        updateUI(data);
    }

    private void updateUI(InfoItem info) {
        if (info == null || binding == null) return;

        // Cập nhật dữ liệu vào Adapter thay vì setText từng TextView
        if (infoAdapter != null) {
            infoAdapter.setStudentData(info);
        }

        // Nếu bạn dùng Glide để load ảnh thẻ:
        // Glide.with(this).load(info.getStudentCardUrl()).into(binding.imgStudentCard);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (networkUtils != null) networkUtils.unregister();
        binding = null;
    }
}