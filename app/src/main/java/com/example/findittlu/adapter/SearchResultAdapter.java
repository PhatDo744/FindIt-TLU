package com.example.findittlu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findittlu.R;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {
    private List<SearchResultItem> itemList;

    public SearchResultAdapter(List<SearchResultItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        SearchResultItem item = itemList.get(position);
        holder.itemImage.setImageResource(item.getImageRes());
        holder.itemTitle.setText(item.getTitle());
        holder.itemCategory.setText(item.getCategory());
        holder.itemLocation.setText(item.getLocation());
        holder.itemDate.setText(item.getDate());
        if (item.isLost()) {
            holder.itemStatus.setText("Đã mất");
            holder.itemStatus.setBackgroundResource(R.drawable.bg_status_lost);
        } else {
            holder.itemStatus.setText("Đã tìm thấy");
            holder.itemStatus.setBackgroundResource(R.drawable.bg_status_found);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemStatus, itemTitle, itemCategory, itemLocation, itemDate;
        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemStatus = itemView.findViewById(R.id.item_status);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemCategory = itemView.findViewById(R.id.item_category);
            itemLocation = itemView.findViewById(R.id.item_location);
            itemDate = itemView.findViewById(R.id.item_date);
        }
    }
} 