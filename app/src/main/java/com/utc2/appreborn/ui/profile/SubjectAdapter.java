package com.utc2.appreborn.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.profile.Subject;

import java.util.List;

public class SubjectAdapter
        extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    List<Subject> list;

    public SubjectAdapter(List<Subject> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        Subject s = list.get(position);

        holder.code.setText(s.getCode());
        holder.name.setText(s.getName());
        holder.credit.setText("Tín chỉ: " + s.getCredit());
        holder.score.setText("Điểm: " + s.getScore());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView code, name, credit, score;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            code = itemView.findViewById(R.id.tvCode);
            name = itemView.findViewById(R.id.tvName);
            credit = itemView.findViewById(R.id.tvCredit);
            score = itemView.findViewById(R.id.tvScore);
        }
    }
}