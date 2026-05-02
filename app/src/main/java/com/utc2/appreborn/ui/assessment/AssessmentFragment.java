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

import com.utc2.appreborn.R;
import com.utc2.appreborn.databinding.FragmentAssessmentBinding;
import com.utc2.appreborn.model.AssessmentPeriod;

import java.util.ArrayList;
import java.util.List;

public class AssessmentFragment extends Fragment {

    private FragmentAssessmentBinding binding;
    private AssessmentViewModel       viewModel;
    private AssessmentAdapter         adapter;

    private int pendingEvidenceCriteriaId = -1;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null && pendingEvidenceCriteriaId != -1) {
                            try {
                                requireContext().getContentResolver()
                                        .takePersistableUriPermission(
                                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } catch (Exception ignored) {}
                            viewModel.updateEvidenceUri(pendingEvidenceCriteriaId, uri.toString());
                            adapter.notifyEvidenceUpdated(pendingEvidenceCriteriaId);
                            Toast.makeText(requireContext(),
                                    R.string.assessment_toast_evidence_attached,
                                    Toast.LENGTH_SHORT).show();
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

        setupBackButton();
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

    private void setupBackButton() {
        binding.btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void setupAdapter() {
        adapter = new AssessmentAdapter(
                updatedList -> viewModel.onScoreChanged(updatedList),
                criteriaId -> {
                    pendingEvidenceCriteriaId = criteriaId;
                    filePickerLauncher.launch("*/*");
                }
        );
        binding.rvCriteria.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCriteria.setAdapter(adapter);
        binding.rvCriteria.setItemAnimator(null);
    }

    private void setupTabSwitcher() {
        // Mặc định: RLSV (Sinh viên) đang chọn → nền đen, CVHT → nền xám
        selectTab(true);

        binding.btnTabRlsv.setOnClickListener(v -> {
            selectTab(true);
            viewModel.switchTab(true);
        });

        binding.btnTabCvht.setOnClickListener(v -> {
            selectTab(false);
            viewModel.switchTab(false);
        });

        // Nút Chọn kỳ → load lại data
        binding.btnChoose.setOnClickListener(v -> {
            boolean isRlsv = Boolean.TRUE.equals(viewModel.getIsStudentTab().getValue());
            viewModel.switchTab(isRlsv);
        });
    }

    /**
     * Cập nhật trạng thái visual của 2 tab.
     * isRlsv = true  → Sinh viên: nền trắng, text đen | Cố vấn: nền đen, text trắng
     * isRlsv = false → ngược lại
     *
     * (Theo ảnh UI: tab đang chọn = nền đen / text trắng)
     */
    private void selectTab(boolean isRlsv) {
        int black = requireContext().getColor(android.R.color.black);
        int white = requireContext().getColor(android.R.color.white);

        binding.btnTabRlsv.setSelected(isRlsv);
        binding.btnTabCvht.setSelected(!isRlsv);

        binding.btnTabRlsv.setBackgroundColor(isRlsv  ? black : 0xFFDDDDDD);
        binding.btnTabCvht.setBackgroundColor(!isRlsv ? black : 0xFFDDDDDD);
        binding.btnTabRlsv.setTextColor(isRlsv  ? white : black);
        binding.btnTabCvht.setTextColor(!isRlsv ? white : black);

        // Ẩn/hiện phần summary phù hợp
        binding.layoutSummaryRlsv.setVisibility(isRlsv ? View.VISIBLE : View.GONE);
        binding.layoutSummaryCvht.setVisibility(isRlsv ? View.GONE   : View.VISIBLE);
        binding.layoutSummaryFull.setVisibility(View.GONE); // chỉ dùng khi cần

        // Cột header: RLSV chỉ cần 4 cột, CVHT (nếu muốn bổ sung cột về sau)
        binding.tvColTapThe.setVisibility(View.GONE);
        binding.tvColKhoa.setVisibility(View.GONE);
        binding.tvColTruong.setVisibility(View.GONE);
    }

    private void setupPeriodDropdown() {
        viewModel.getPeriods().observe(getViewLifecycleOwner(), periods -> {
            if (periods == null || periods.isEmpty()) return;

            List<String> labels = new ArrayList<>();
            for (AssessmentPeriod p : periods) labels.add(p.getLabel());

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    labels);

            binding.actvPeriod.setAdapter(spinnerAdapter);
            binding.actvPeriod.setText(labels.get(0), false);
            viewModel.setSelectedPeriod(periods.get(0));

            binding.actvPeriod.setOnItemClickListener((parent, v, pos, id) ->
                    viewModel.setSelectedPeriod(periods.get(pos)));
        });
    }

    // ─── Observers ────────────────────────────────────────────────────────────

    private void observeViewModel() {
        viewModel.getCriteria().observe(getViewLifecycleOwner(), list -> {
            binding.progressBar.setVisibility(View.GONE);
            adapter.submitList(list);
        });

        viewModel.getTotalScore().observe(getViewLifecycleOwner(), score -> {
            String formatted = formatScore(score);
            binding.etTotalScore.setText(formatted);
            binding.etTotalScoreRlsv.setText(formatted);
        });

        viewModel.getClassification().observe(getViewLifecycleOwner(), cls -> {
            binding.tvClassification.setText(cls);
            binding.tvClassRlsv.setText(cls);
        });

        viewModel.getStudentCode().observe(getViewLifecycleOwner(),
                code -> binding.tvStudentCode.setText(code));

        viewModel.getAdvisorName().observe(getViewLifecycleOwner(),
                name -> binding.tvAdvisorName.setText(name));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String formatScore(float v) {
        if (v == (int) v) return String.valueOf((int) v);
        return String.valueOf(v);
    }
}