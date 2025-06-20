package com.example.findittlu.ui.profile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.findittlu.R;
import com.example.findittlu.data.model.Post;
import com.google.android.material.button.MaterialButton;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.PostViewHolder> {

    private Context context;
    private List<Post> posts;

    public MyPostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        android.util.Log.d("DEBUG_Adapter", "Bind post: " + (post == null ? "null" : post.getTitle()));
        if (post != null) {
            holder.titleTextView.setText(post.getTitle());
            holder.dateTextView.setText(post.getDate());
            String statusText = post.getStatusText();
            holder.statusChip.setText(statusText);
            // Đổi màu chữ và nền theo trạng thái
            if ("Đang tìm".equals(statusText)) {
                holder.statusChip.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.lost_red));
                holder.statusChip.setBackgroundResource(R.drawable.status_searching_background);
            } else if ("Đang giữ".equals(statusText)) {
                holder.statusChip.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.found_green));
                holder.statusChip.setBackgroundResource(R.drawable.status_found_background);
            } else if ("Đã xong".equals(statusText)) {
                holder.statusChip.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary));
                holder.statusChip.setBackgroundResource(R.drawable.status_completed_background);
            } else {
                holder.statusChip.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary));
                holder.statusChip.setBackgroundResource(0);
            }

            // Hiển thị cảnh báo nếu sắp hết hạn
            if (post.isExpiringSoon()) {
                holder.expiryWarningTextView.setVisibility(View.VISIBLE);
                holder.expiryWarningTextView.setText("⚠️ Sắp hết hạn (còn " + post.getDaysUntilExpiration() + " ngày)");
                holder.expiryWarningTextView.setTextColor(ContextCompat.getColor(context, R.color.warning_orange));
            } else {
                holder.expiryWarningTextView.setVisibility(View.GONE);
            }

            // Hiển thị nút theo trạng thái
            if (post.getStatus().equalsIgnoreCase("COMPLETED")) {
                holder.deleteButton.setVisibility(View.VISIBLE);
                holder.editButton.setVisibility(View.GONE);
                holder.completeButton.setVisibility(View.GONE);
            } else {
                holder.deleteButton.setVisibility(View.VISIBLE);
                holder.editButton.setVisibility(View.VISIBLE);
                holder.completeButton.setVisibility(View.VISIBLE);
            }
            
            // Xử lý hình ảnh nếu có
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                // Load hình ảnh đầu tiên nếu có
                // TODO: Sử dụng Glide/Picasso để load ảnh từ URL
                holder.imageView.setVisibility(View.VISIBLE);
            } else {
                holder.imageView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updateData(List<Post> newPosts) {
        android.util.Log.d("DEBUG_Adapter", "updateData: newPosts size = " + (newPosts == null ? "null" : newPosts.size()));
        if (newPosts == null) {
            this.posts = new java.util.ArrayList<>();
        } else {
            this.posts = new java.util.ArrayList<>(newPosts);
        }
        notifyDataSetChanged();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView dateTextView;
        public TextView expiryWarningTextView;
        public com.google.android.material.textview.MaterialTextView statusChip;
        public ImageView imageView;
        public MaterialButton editButton, deleteButton, completeButton;

        @SuppressLint("WrongViewCast")
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.itemTitleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            expiryWarningTextView = itemView.findViewById(R.id.expiryWarningTextView);
            statusChip = itemView.findViewById(R.id.statusTextView);
            imageView = itemView.findViewById(R.id.itemImageView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            completeButton = itemView.findViewById(R.id.completeButton);
        }
    }
} 