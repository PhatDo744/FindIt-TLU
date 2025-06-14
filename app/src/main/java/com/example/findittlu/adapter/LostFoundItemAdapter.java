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
import com.example.findittlu.ui.common.DetailFragment;

public class LostFoundItemAdapter extends RecyclerView.Adapter<LostFoundItemAdapter.LostFoundItemViewHolder> {

    private List<LostFoundItem> itemList;
    private Context context;
    private androidx.fragment.app.Fragment fragment;

    public LostFoundItemAdapter(androidx.fragment.app.Fragment fragment, List<LostFoundItem> itemList) {
        this.fragment = fragment;
        this.itemList = itemList;
        this.context = fragment.requireContext();
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
            android.os.Bundle args = new android.os.Bundle();
            args.putString("title", item.getTitle());
            args.putString("location", item.getLocation());
            args.putString("date", item.getDate());
            args.putBoolean("isLost", item.isLost());
            args.putInt("imageRes", item.getImageUrl());
            androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(holder.itemView);
            navController.navigate(com.example.findittlu.R.id.detailFragment, args);
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