package com.utc2.appreborn.ui.Info.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.Info.model.InfoItem; // Model bạn đã gửi
import java.util.ArrayList;
import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {

    // Lớp phụ để định nghĩa cấu trúc một hàng hiển thị
    private static class DisplayRow {
        String label;
        String value;
        DisplayRow(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }

    private final List<DisplayRow> displayRows = new ArrayList<>();

    // Hàm nhận dữ liệu từ Model InfoItem và chuyển đổi thành List hiển thị
    public void setStudentData(InfoItem item) {
        displayRows.clear();
        if (item != null) {
            displayRows.add(new DisplayRow("Nơi sinh", item.getBirthPlace()));
            displayRows.add(new DisplayRow("Số CCCD", item.getCccd()));
            displayRows.add(new DisplayRow("Thường trú", item.getPermanentAddress()));
            displayRows.add(new DisplayRow("Tạm trú", item.getTempAddress()));
            displayRows.add(new DisplayRow("Hiện tại", item.getCurrentAddress()));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_info_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisplayRow row = displayRows.get(position);
        holder.tvLabel.setText(row.label);
        holder.tvValue.setText(row.value);
    }

    @Override
    public int getItemCount() {
        return displayRows.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvValue;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvValue = itemView.findViewById(R.id.tvValue);
        }
    }
}