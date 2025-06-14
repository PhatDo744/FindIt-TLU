package com.example.findittlu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.content.Context;

import com.example.findittlu.R;
import com.example.findittlu.ui.activity.DetailActivity;

public class LostFoundItemAdapter extends RecyclerView.Adapter<LostFoundItemAdapter.LostFoundItemViewHolder> {

    private List<LostFoundItem> itemList;
    private Context context;

    public LostFoundItemAdapter(Context context, List<LostFoundItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public LostFoundItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lost_found, parent, false);
        return new LostFoundItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostFoundItemViewHolder holder, int position) {
        LostFoundItem item = itemList.get(position);

        holder.itemImage.setImageResource(item.getImageUrl());
        holder.itemTitle.setText(item.getTitle());
        holder.itemLocation.setText(item.getLocation());
        holder.itemDate.setText(item.getDate());

        // Set status text and background color
        if (item.isLost()) {
            holder.itemStatus.setText("Đã mất");
            holder.itemStatus.setBackgroundResource(R.drawable.bg_status_lost);
        } else {
            holder.itemStatus.setText("Đã tìm thấy");
            holder.itemStatus.setBackgroundResource(R.drawable.bg_status_found);
        }

        // Sự kiện click mở chi tiết
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, DetailActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("location", item.getLocation());
            intent.putExtra("date", item.getDate());
            intent.putExtra("isLost", item.isLost());
            intent.putExtra("imageRes", item.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class LostFoundItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView itemLocation;
        TextView itemStatus;
        TextView itemDate;

        public LostFoundItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemLocation = itemView.findViewById(R.id.item_location);
            itemStatus = itemView.findViewById(R.id.item_status);
            itemDate = itemView.findViewById(R.id.item_date);
        }
    }
} 