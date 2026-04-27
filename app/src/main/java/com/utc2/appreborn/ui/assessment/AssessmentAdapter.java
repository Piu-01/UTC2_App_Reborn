// PATH: app/src/main/java/com/utc2/appreborn/ui/assessment/AssessmentAdapter.java

package com.utc2.appreborn.ui.assessment;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.utc2.appreborn.R;
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

    private List<AssessmentCriteria>  items = new ArrayList<>();
    private OnScoreChangedListener    scoreListener;
    private OnEvidenceClickListener   evidenceListener;

    public AssessmentAdapter(OnScoreChangedListener scoreListener,
                             OnEvidenceClickListener evidenceListener) {
        this.scoreListener    = scoreListener;
        this.evidenceListener = evidenceListener;
    }

    // ─── Data ─────────────────────────────────────────────────────────────────

    public void submitList(List<AssessmentCriteria> newList) {
        this.items = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<AssessmentCriteria> getItems() { return items; }

    /** Cập nhật icon minh chứng cho đúng item sau khi user chọn file */
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
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case AssessmentCriteria.TYPE_SECTION_HEADER:
                return new HeaderViewHolder(
                        ItemAssessmentHeaderBinding.inflate(inflater, parent, false));
            case AssessmentCriteria.TYPE_DEDUCTION:
                return new DeductionViewHolder(
                        ItemAssessmentDeductionBinding.inflate(inflater, parent, false));
            default: // TYPE_CRITERIA
                return new CriteriaViewHolder(
                        ItemAssessmentCriteriaBinding.inflate(inflater, parent, false));
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
                ((CriteriaViewHolder) holder).bind(item);
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

        void bind(AssessmentCriteria item) {
            b.tvCriteriaTitle.setText(item.getTitle());
            b.tvMaxScore.setText(formatScore(item.getMaxScore()));

            // ── Dropdown score options ────────────────────────────────────────
            setupScoreDropdown(b.actvScore, item);

            // ── Minh chứng (evidence) ─────────────────────────────────────────
            if (item.isRequiresEvidence()) {
                b.btnEvidence.setVisibility(View.VISIBLE);
                updateEvidenceButton(item);
                b.btnEvidence.setOnClickListener(v -> {
                    if (evidenceListener != null) {
                        evidenceListener.onEvidenceClick(item.getId());
                    }
                });
            } else {
                b.btnEvidence.setVisibility(View.GONE);
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
            }
        }

        private void setupScoreDropdown(MaterialAutoCompleteTextView actv,
                                        AssessmentCriteria item) {
            Context ctx = actv.getContext();

            // Build display list (chuyển float sang chuỗi gọn)
            List<String> displayOpts = new ArrayList<>();
            if (item.getScoreOptions() != null) {
                for (Float f : item.getScoreOptions()) {
                    displayOpts.add(formatScore(f));
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    ctx, android.R.layout.simple_dropdown_item_1line, displayOpts);
            actv.setAdapter(adapter);

            // Gỡ watcher cũ trước khi set text để tránh callback sai
            if (activeWatcher != null) actv.removeTextChangedListener(activeWatcher);
            actv.setText(formatScore(item.getCurrentScore()), false);

            activeWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                @Override public void onTextChanged(CharSequence s, int st, int b2, int c)  {}
                @Override
                public void afterTextChanged(Editable s) {
                    String raw = s.toString().trim();
                    try {
                        float val = Float.parseFloat(raw);
                        item.setCurrentScore(val);
                        if (scoreListener != null) {
                            scoreListener.onScoreChanged(items);
                        }
                    } catch (NumberFormatException ignored) { /* người dùng đang gõ dở */ }
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
            b.tvPenalty.setText(formatScore(item.getMaxScore()));

            setupScoreDropdown(b.actvDeductionScore, item);
        }

        private void setupScoreDropdown(MaterialAutoCompleteTextView actv,
                                        AssessmentCriteria item) {
            Context ctx = actv.getContext();
            List<String> displayOpts = new ArrayList<>();
            if (item.getScoreOptions() != null) {
                for (Float f : item.getScoreOptions()) {
                    displayOpts.add(formatScore(f));
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    ctx, android.R.layout.simple_dropdown_item_1line, displayOpts);
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

    /** Loại bỏ .0 nếu là số nguyên để hiển thị gọn hơn */
    private String formatScore(float v) {
        if (v == (int) v) return String.valueOf((int) v);
        return String.valueOf(v);
    }
}