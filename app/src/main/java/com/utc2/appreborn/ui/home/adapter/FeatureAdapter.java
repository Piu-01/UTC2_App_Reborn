package com.utc2.appreborn.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.home.model.FeatureItem;

import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {

    public interface OnFeatureClickListener {
        void onFeatureClick(String featureId);
    }

    private final List<FeatureItem>      items;
    private final OnFeatureClickListener listener;

    public FeatureAdapter(List<FeatureItem> items, OnFeatureClickListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feature_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeatureItem item = items.get(position);
        holder.icon.setImageResource(item.getIconRes());
        holder.title.setText(item.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onFeatureClick(item.getId()));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView  title;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon  = itemView.findViewById(R.id.iv_feature_icon);
            title = itemView.findViewById(R.id.tv_feature_title);
        }
    }
}
