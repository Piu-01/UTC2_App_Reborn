package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.Info.InfoFragment;
import com.utc2.appreborn.ui.profile.TrainingProgram.TrainingProgramActivity;
import com.utc2.appreborn.utils.NetworkUtils;

public class ProfileFragment extends Fragment {

    private LinearLayout layoutSubjectList, layoutGraduationReq;
    private AppCompatButton btnInfo, btnChangePassword;
    private ImageView btnNotification;
    private TextView tvStudentName, tvStudentId;
    private NetworkUtils networkUtils;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp đúng file layout thiết kế bạn đã gửi
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupNetworkMonitoring();
        setClickListeners();
    }

    private void initViews(View view) {
        // Ánh xạ các View theo đúng ID trong XML thiết kế của bạn
        layoutSubjectList = view.findViewById(R.id.layoutSubjectList);
        layoutGraduationReq = view.findViewById(R.id.layoutGraduationReq);
        btnInfo = view.findViewById(R.id.btnProfileInfo);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnNotification = view.findViewById(R.id.btnNotification);
        tvStudentName = view.findViewById(R.id.tvStudentName);
        tvStudentId = view.findViewById(R.id.tvStudentId);
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(requireContext(), new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                if (isAdded()) {
                    btnChangePassword.setEnabled(true);
                    btnChangePassword.setAlpha(1.0f);
                }
            }

            @Override
            public void onNetworkLost() {
                if (isAdded()) {
                    showToast("Bạn đang ngoại tuyến.");
                    btnChangePassword.setEnabled(false);
                    btnChangePassword.setAlpha(0.5f);
                }
            }
        });
        networkUtils.register();
    }

    private void setClickListeners() {
        // Xử lý chuyển sang InfoFragment (Dùng FragmentTransaction để giữ cấu trúc Fragment)
        btnInfo.setOnClickListener(v -> {
            InfoFragment infoFragment = new InfoFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            // Thay đổi R.id.fragment_container thành ID FrameLayout chính trong MainActivity của bạn
            transaction.replace(R.id.fragment_container, infoFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnChangePassword.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                startActivity(new Intent(requireContext(), ChangePasswordActivity.class));
            } else {
                showToast("Vui lòng kết nối mạng để đổi mật khẩu!");
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
        if (isAdded()) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
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