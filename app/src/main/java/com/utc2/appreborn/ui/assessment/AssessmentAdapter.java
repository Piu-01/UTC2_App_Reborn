package com.utc2.appreborn.ui.assessment;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.utc2.appreborn.databinding.ItemAssessmentCriteriaBinding;
import com.utc2.appreborn.databinding.ItemAssessmentDeductionBinding;
import com.utc2.appreborn.databinding.ItemAssessmentHeaderBinding;
import com.utc2.appreborn.model.AssessmentCriteria;

import java.util.ArrayList;
import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // ─── Callback Interfaces ──────────────────────────────────────────────────

    public interface OnScoreChangedListener {
        void onScoreChanged(List<AssessmentCriteria> updatedList);
    }

    public interface OnEvidenceClickListener {
        void onEvidenceClick(int criteriaId);
    }

    // ─── Fields ───────────────────────────────────────────────────────────────

    private List<AssessmentCriteria> items = new ArrayList<>();
    private OnScoreChangedListener   scoreListener;
    private OnEvidenceClickListener  evidenceListener;

    /** Đếm STT thực (bỏ qua HEADER) để hiển thị đúng số thứ tự */
    private List<Integer> sttMap = new ArrayList<>();

    public AssessmentAdapter(OnScoreChangedListener scoreListener,
                             OnEvidenceClickListener evidenceListener) {
        this.scoreListener    = scoreListener;
        this.evidenceListener = evidenceListener;
    }

    // ─── Data ─────────────────────────────────────────────────────────────────

    public void submitList(List<AssessmentCriteria> newList) {
        this.items = newList != null ? newList : new ArrayList<>();
        rebuildSttMap();
        notifyDataSetChanged();
    }

    /** Xây dựng bảng ánh xạ vị trí → STT thực (bỏ qua header và deduction) */
    private void rebuildSttMap() {
        sttMap.clear();
        int stt = 0;
        for (AssessmentCriteria item : items) {
            if (item.getViewType() == AssessmentCriteria.TYPE_CRITERIA) {
                stt++;
                sttMap.add(stt);
            } else {
                sttMap.add(0); // 0 = không hiển thị STT
            }
        }
    }

    public List<AssessmentCriteria> getItems() { return items; }

    public void notifyEvidenceUpdated(int criteriaId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == criteriaId) {
                notifyItemChanged(i);
                return;
            }
        }
    }

    // ─── RecyclerView ─────────────────────────────────────────────────────────

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case AssessmentCriteria.TYPE_SECTION_HEADER:
                return new HeaderViewHolder(
                        ItemAssessmentHeaderBinding.inflate(inf, parent, false));
            case AssessmentCriteria.TYPE_DEDUCTION:
                return new DeductionViewHolder(
                        ItemAssessmentDeductionBinding.inflate(inf, parent, false));
            default:
                return new CriteriaViewHolder(
                        ItemAssessmentCriteriaBinding.inflate(inf, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AssessmentCriteria item = items.get(position);
        switch (item.getViewType()) {
            case AssessmentCriteria.TYPE_SECTION_HEADER:
                ((HeaderViewHolder) holder).bind(item);
                break;
            case AssessmentCriteria.TYPE_DEDUCTION:
                ((DeductionViewHolder) holder).bind(item);
                break;
            default:
                int stt = (position < sttMap.size()) ? sttMap.get(position) : 0;
                ((CriteriaViewHolder) holder).bind(item, stt);
                break;
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ─── ViewHolder: Section Header ───────────────────────────────────────────

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final ItemAssessmentHeaderBinding b;

        HeaderViewHolder(ItemAssessmentHeaderBinding binding) {
            super(binding.getRoot());
            b = binding;
        }

        void bind(AssessmentCriteria item) {
            b.tvSectionTitle.setText(item.getTitle());
            // Tiêu đề mục trừ điểm dùng màu đỏ
            if (item.getTitle() != null && item.getTitle().contains("TRỪ ĐIỂM")) {
                b.tvSectionTitle.setTextColor(
                        b.getRoot().getContext().getColor(android.R.color.holo_red_dark));
            } else {
                b.tvSectionTitle.setTextColor(0xFF0057A8); // xanh dương
            }
        }
    }

    // ─── ViewHolder: Criteria ─────────────────────────────────────────────────

    class CriteriaViewHolder extends RecyclerView.ViewHolder {
        private final ItemAssessmentCriteriaBinding b;
        private TextWatcher activeWatcher;

        CriteriaViewHolder(ItemAssessmentCriteriaBinding binding) {
            super(binding.getRoot());
            b = binding;
        }

        void bind(AssessmentCriteria item, int stt) {
            // STT
            b.tvStt.setText(stt > 0 ? String.valueOf(stt) : "");

            // Nội dung
            b.tvCriteriaTitle.setText(item.getTitle());

            // Điểm tối đa
            b.tvMaxScore.setText(formatScore(item.getMaxScore()));

            // Dropdown điểm
            setupScoreDropdown(b.actvScore, item);

            // Minh chứng
            if (item.isRequiresEvidence()) {
                b.layoutEvidence.setVisibility(View.VISIBLE);
                updateEvidenceButton(item);
                b.btnEvidence.setOnClickListener(v -> {
                    if (evidenceListener != null) evidenceListener.onEvidenceClick(item.getId());
                });
            } else {
                b.layoutEvidence.setVisibility(View.GONE);
            }
        }

        private void updateEvidenceButton(AssessmentCriteria item) {
            if (item.hasEvidence()) {
                b.btnEvidence.setText("✓ Đã nộp MC");
                b.btnEvidence.setTextColor(
                        b.getRoot().getContext().getColor(android.R.color.holo_green_dark));
            } else {
                b.btnEvidence.setText("Nộp MC");
                b.btnEvidence.setTypeface(b.btnEvidence.getTypeface(), Typeface.BOLD);
                b.btnEvidence.setTextColor(
                        b.getRoot().getContext().getColor(android.R.color.black));
            }
        }

        private void setupScoreDropdown(MaterialAutoCompleteTextView actv,
                                        AssessmentCriteria item) {
            Context ctx = actv.getContext();
            List<String> opts = new ArrayList<>();
            if (item.getScoreOptions() != null) {
                for (Float f : item.getScoreOptions()) opts.add(formatScore(f));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    ctx, android.R.layout.simple_dropdown_item_1line, opts);
            actv.setAdapter(adapter);

            if (activeWatcher != null) actv.removeTextChangedListener(activeWatcher);
            actv.setText(formatScore(item.getCurrentScore()), false);

            activeWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
                @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        float val = Float.parseFloat(s.toString().trim());
                        item.setCurrentScore(val);
                        if (scoreListener != null) scoreListener.onScoreChanged(items);
                    } catch (NumberFormatException ignored) {}
                }
            };
            actv.addTextChangedListener(activeWatcher);
        }
    }

    // ─── ViewHolder: Deduction ────────────────────────────────────────────────

    class DeductionViewHolder extends RecyclerView.ViewHolder {
        private final ItemAssessmentDeductionBinding b;
        private TextWatcher activeWatcher;

        DeductionViewHolder(ItemAssessmentDeductionBinding binding) {
            super(binding.getRoot());
            b = binding;
        }

        void bind(AssessmentCriteria item) {
            b.tvDeductionTitle.setText(item.getTitle());
            b.tvMaxDeduction.setText(formatScore(item.getMaxScore()));
            setupScoreDropdown(b.actvDeductionScore, item);
        }

        private void setupScoreDropdown(MaterialAutoCompleteTextView actv,
                                        AssessmentCriteria item) {
            Context ctx = actv.getContext();
            List<String> opts = new ArrayList<>();
            if (item.getScoreOptions() != null) {
                for (Float f : item.getScoreOptions()) opts.add(formatScore(f));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    ctx, android.R.layout.simple_dropdown_item_1line, opts);
            actv.setAdapter(adapter);

            if (activeWatcher != null) actv.removeTextChangedListener(activeWatcher);
            actv.setText(formatScore(item.getCurrentScore()), false);

            activeWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
                @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        float val = Float.parseFloat(s.toString().trim());
                        item.setCurrentScore(val);
                        if (scoreListener != null) scoreListener.onScoreChanged(items);
                    } catch (NumberFormatException ignored) {}
                }
            };
            actv.addTextChangedListener(activeWatcher);
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String formatScore(float v) {
        if (v == (int) v) return String.valueOf((int) v);
        return String.valueOf(v);
    }
}