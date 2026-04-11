package com.utc2.appreborn.ui.tuition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import java.util.List;

public class SubjectTuitionAdapter extends RecyclerView.Adapter<SubjectTuitionAdapter.ViewHolder> {

    private List<SubjectTuition> subjectList;

    public SubjectTuitionAdapter(List<SubjectTuition> subjectList) {
        this.subjectList = subjectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_tuition, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubjectTuition item = subjectList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDetails.setText(item.getDetails());
        holder.tvAmount.setText(item.getAmount());
        holder.tvStatus.setText(item.getStatus());

        // Bạn có thể thêm logic đổi màu Status tại đây nếu muốn
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvAmount, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSubjectName);
            tvDetails = itemView.findViewById(R.id.tvSubjectDetails);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}