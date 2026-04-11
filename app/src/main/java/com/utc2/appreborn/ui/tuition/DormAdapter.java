package com.utc2.appreborn.ui.tuition;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import java.util.List;

public class DormAdapter extends RecyclerView.Adapter<DormAdapter.ViewHolder> {
    private List<DormTuition> dormList;

    public DormAdapter(List<DormTuition> dormList) { this.dormList = dormList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dorm_tuition, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DormTuition item = dormList.get(position);
        holder.tvRoomName.setText(item.getRoomName());
        holder.tvDormDetails.setText(item.getDetails());
        holder.tvDormAmount.setText(item.getAmount());
    }

    @Override
    public int getItemCount() { return dormList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvDormDetails, tvDormAmount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvDormDetails = itemView.findViewById(R.id.tvDormDetails);
            tvDormAmount = itemView.findViewById(R.id.tvDormAmount);
        }
    }
}