package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment; // Đổi sang Fragment

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.Info.InfoActivity;
import com.utc2.appreborn.ui.profile.TrainingProgram.TrainingProgramActivity;
import com.utc2.appreborn.utils.NetworkUtils;

public class ProfileFragment extends Fragment {

    private LinearLayout layoutSubjectList, layoutGraduationReq;
    private AppCompatButton btnInfo, btnChangePassword;
    private ImageView btnNotification;
    private NetworkUtils networkUtils;

    // 1. Fragment dùng onCreateView để nạp Layout XML
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // 2. Fragment dùng onViewCreated để ánh xạ View và bắt sự kiện
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupNetworkMonitoring();
        setClickListeners();
    }

    private void initViews(View view) {
        // PHẢI dùng view.findViewById
        layoutSubjectList = view.findViewById(R.id.layoutSubjectList);
        layoutGraduationReq = view.findViewById(R.id.layoutGraduationReq);
        btnInfo = view.findViewById(R.id.btnProfileInfo);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnNotification = view.findViewById(R.id.btnNotification);

        // XÓA: Không tìm bottom_bar_compose ở đây nữa vì nó nằm ở MainActivity rồi
        View bottomBar = view.findViewById(R.id.bottom_bar_compose);
        if (bottomBar != null) bottomBar.setVisibility(View.GONE);
    }

    private void setupNetworkMonitoring() {
        // Dùng requireContext() thay cho this
        networkUtils = new NetworkUtils(requireContext(), new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                btnChangePassword.setEnabled(true);
                btnChangePassword.setAlpha(1.0f);
            }

            @Override
            public void onNetworkLost() {
                showToast("Bạn đang ngoại tuyến. Một số tính năng sẽ bị hạn chế.");
                btnChangePassword.setEnabled(false);
                btnChangePassword.setAlpha(0.5f);
            }
        });
        networkUtils.register();
    }

    private void setClickListeners() {
        // Chuyển Activity dùng requireContext()
        btnInfo.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), InfoActivity.class));
        });

        btnChangePassword.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                 startActivity(new Intent(requireContext(), ChangePasswordActivity.class));
            } else {
                showToast("Vui lòng kết nối mạng!");
            }
        });

        layoutSubjectList.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), TrainingProgramActivity.class));
        });

        layoutGraduationReq.setOnClickListener(v -> {
             startActivity(new Intent(requireContext(), GraduationRequirementsActivity.class));
        });

        btnNotification.setOnClickListener(v -> showToast("Không có thông báo mới"));
    }

    private void showToast(String msg) {
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (networkUtils != null) {
            networkUtils.unregister();
        }
    }
}