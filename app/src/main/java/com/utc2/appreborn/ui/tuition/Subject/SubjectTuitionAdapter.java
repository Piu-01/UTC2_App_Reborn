package com.utc2.appreborn.ui.tuition.Subject;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import java.util.List;
import java.util.Locale; // Import thêm cái này

public class SubjectTuitionAdapter extends RecyclerView.Adapter<SubjectTuitionAdapter.ViewHolder> {

    // Sửa cảnh báo 'final'
    private final List<SubjectTuition> subjectList;

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

        // Sửa lỗi 'Implicitly using the default locale' bằng cách thêm Locale.US hoặc Locale.getDefault()
        holder.tvAmount.setText(String.format(Locale.US, "%,d VND", item.getAmount()));

        // Vì trang này chỉ hiện môn chưa đóng, mình mặc định hiện chữ đỏ luôn
        holder.tvStatus.setText(holder.itemView.getContext().getString(R.string.status_unpaid));
        holder.tvStatus.setTextColor(Color.RED);
    }

    @Override
    public int getItemCount() {
        return subjectList != null ? subjectList.size() : 0;
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