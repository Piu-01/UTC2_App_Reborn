// PATH: app/src/main/java/com/utc2/appreborn/ui/assessment/AssessmentFragment.java

package com.utc2.appreborn.ui.assessment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.utc2.appreborn.databinding.FragmentAssessmentBinding;
import com.utc2.appreborn.model.AssessmentPeriod;

import java.util.ArrayList;
import java.util.List;

public class AssessmentFragment extends Fragment {

    // ─── Fields ───────────────────────────────────────────────────────────────

    private FragmentAssessmentBinding binding;
    private AssessmentViewModel       viewModel;
    private AssessmentAdapter         adapter;

    /** ID tiêu chí đang chờ người dùng chọn file minh chứng */
    private int pendingEvidenceCriteriaId = -1;

    /** ActivityResultLauncher để mở file picker (GetContent) */
    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null && pendingEvidenceCriteriaId != -1) {
                            // Giữ quyền đọc file lâu dài
                            try {
                                requireContext().getContentResolver()
                                        .takePersistableUriPermission(
                                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } catch (Exception ignored) {}

                            viewModel.updateEvidenceUri(pendingEvidenceCriteriaId, uri.toString());
                            adapter.notifyEvidenceUpdated(pendingEvidenceCriteriaId);
                            Toast.makeText(requireContext(),
                                    "Đã đính kèm minh chứng", Toast.LENGTH_SHORT).show();
                            pendingEvidenceCriteriaId = -1;
                        }
                    }
            );

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAssessmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AssessmentViewModel.class);

        setupAdapter();
        setupTabSwitcher();
        setupPeriodDropdown();
        observeViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ─── Setup ────────────────────────────────────────────────────────────────

    private void setupAdapter() {
        adapter = new AssessmentAdapter(
                // Score changed → cập nhật tổng điểm
                updatedList -> viewModel.onScoreChanged(updatedList),

                // Evidence clicked → lưu id và mở file picker
                criteriaId -> {
                    pendingEvidenceCriteriaId = criteriaId;
                    // Cho phép chọn mọi loại file (ảnh, PDF…)
                    filePickerLauncher.launch("*/*");
                }
        );

        binding.rvCriteria.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCriteria.setAdapter(adapter);
        // Tắt animation khi update score để tránh nhấp nháy
        binding.rvCriteria.setItemAnimator(null);
    }

    private void setupTabSwitcher() {
        // Mặc định: tab RLSV active
        selectTab(true);

        binding.btnTabRlsv.setOnClickListener(v -> {
            selectTab(true);
            viewModel.switchTab(true);
        });

        binding.btnTabCvht.setOnClickListener(v -> {
            selectTab(false);
            viewModel.switchTab(false);
        });
    }

    private void selectTab(boolean isRlsv) {
        binding.btnTabRlsv.setSelected(isRlsv);
        binding.btnTabCvht.setSelected(!isRlsv);
        binding.btnTabRlsv.setTextColor(requireContext().getColor(
                isRlsv ? android.R.color.white : android.R.color.black));
        binding.btnTabCvht.setTextColor(requireContext().getColor(
                isRlsv ? android.R.color.black : android.R.color.white));
    }

    private void setupPeriodDropdown() {
        viewModel.getPeriods().observe(getViewLifecycleOwner(), periods -> {
            if (periods == null || periods.isEmpty()) return;

            List<String> labels = new ArrayList<>();
            for (AssessmentPeriod p : periods) labels.add(p.getLabel());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    labels);

            binding.actvPeriod.setAdapter(adapter);
            // Chọn mặc định học kỳ đầu tiên
            binding.actvPeriod.setText(labels.get(0), false);
            viewModel.setSelectedPeriod(periods.get(0));

            binding.actvPeriod.setOnItemClickListener((parent, v, pos, id) ->
                    viewModel.setSelectedPeriod(periods.get(pos)));
        });
    }

    // ─── Observers ────────────────────────────────────────────────────────────

    private void observeViewModel() {
        // Danh sách tiêu chí
        viewModel.getCriteria().observe(getViewLifecycleOwner(), list -> {
            binding.progressBar.setVisibility(View.GONE);
            adapter.submitList(list);
        });

        // Tổng điểm & xếp loại (real-time)
        viewModel.getTotalScore().observe(getViewLifecycleOwner(), score -> {
            String display = formatScore(score);
            binding.tvTotalScore.setText(display);
        });

        viewModel.getClassification().observe(getViewLifecycleOwner(), cls -> {
            binding.tvClassification.setText(cls);
        });

        // Thông tin sinh viên
        viewModel.getStudentCode().observe(getViewLifecycleOwner(), code ->
                binding.tvStudentCode.setText(code));

        viewModel.getAdvisorName().observe(getViewLifecycleOwner(), name ->
                binding.tvAdvisorName.setText(name));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String formatScore(float v) {
        if (v == (int) v) return String.valueOf((int) v);
        return String.valueOf(v);
    }
}