package com.utc2.appreborn.ui.tuition.Dorm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import java.util.List;
import java.util.Locale;

public class DormAdapter extends RecyclerView.Adapter<DormAdapter.ViewHolder> {
    private final List<DormTuition> dormList;

    public DormAdapter(List<DormTuition> dormList) {
        this.dormList = dormList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dorm_tuition, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DormTuition item = dormList.get(position);

        // Kiểm tra null trước khi set text để tuyệt đối không crash
        if (holder.tvRoomName != null) {
            holder.tvRoomName.setText(item.getRoomName());
        }

        if (holder.tvDormDetails != null) {
            holder.tvDormDetails.setText(item.getDetails());
        }

        if (holder.tvDormAmount != null) {
            // Định dạng tiền tệ: 650,000 VND
            String formattedAmount = String.format(Locale.getDefault(), "%,d VND", item.getAmount());
            holder.tvDormAmount.setText(formattedAmount);
        }
    }

    @Override
    public int getItemCount() {
        return dormList != null ? dormList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvDormDetails, tvDormAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // KHỚP ID VỚI XML CỦA BẠN:
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvDormDetails = itemView.findViewById(R.id.tvDormDetails);
            tvDormAmount = itemView.findViewById(R.id.tvDormAmount); // Phải là tvDormAmount
        }
    }
}