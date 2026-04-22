package com.utc2.appreborn.ui.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.model.AcademicWarning;

import java.util.List;

/**
 * WarningAdapter
 * - NGHIÊM TRỌNG: nền đỏ nhạt + ImageView ic_warning_triangle góc trên phải
 * - Thường: nền trắng + viền nhạt, không có badge
 */
public class WarningAdapter extends RecyclerView.Adapter<WarningAdapter.WarningViewHolder> {

    private final List<AcademicWarning> warningList;

    public WarningAdapter(List<AcademicWarning> warningList) {
        this.warningList = warningList;
    }

    public void updateList(List<AcademicWarning> newList) {
        warningList.clear();
        warningList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WarningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_warning, parent, false);
        return new WarningViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WarningViewHolder holder, int position) {
        holder.bind(warningList.get(position));
    }

    @Override
    public int getItemCount() {
        return warningList.size();
    }

    static class WarningViewHolder extends RecyclerView.ViewHolder {

        private final CardView   cardRoot;
        private final TextView   tvTitle;
        private final TextView   tvSubTitle;
        private final TextView   tvDate;
        private final ImageView  ivSeriousBadge;  // ImageView thay vì TextView

        WarningViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot       = itemView.findViewById(R.id.card_warning_item);
            tvTitle        = itemView.findViewById(R.id.tv_warning_title);
            tvSubTitle     = itemView.findViewById(R.id.tv_warning_subtitle);
            tvDate         = itemView.findViewById(R.id.tv_warning_date);
            ivSeriousBadge = itemView.findViewById(R.id.iv_serious_badge);  // id mới
        }

        void bind(AcademicWarning warning) {
            tvTitle.setText(warning.getTitle());
            tvDate.setText(warning.getDate());

            if (warning.getSubTitle() != null && !warning.getSubTitle().isEmpty()) {
                tvSubTitle.setText(warning.getSubTitle());
                tvSubTitle.setVisibility(View.VISIBLE);
            } else {
                tvSubTitle.setVisibility(View.GONE);
            }

            if (warning.isSerious()) {
                // Nền đỏ nhạt + icon badge
                cardRoot.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
                ivSeriousBadge.setVisibility(View.VISIBLE);
            } else {
                // Nền trắng + không có badge
                cardRoot.setCardBackgroundColor(Color.WHITE);
                ivSeriousBadge.setVisibility(View.GONE);
            }
        }
    }
}
