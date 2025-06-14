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
        if (post != null) {
            holder.titleTextView.setText(post.getTitle());
            holder.dateTextView.setText(post.getDate());
            holder.statusChip.setText(post.getStatusText());

            // Set màu và nền cho trạng thái
            switch (post.getStatus().toUpperCase()) {
                case "SEARCHING":
                    holder.statusChip.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.lost_red));
                    holder.statusChip.setBackgroundResource(R.drawable.status_searching_background);
                    break;
                case "FOUND":
                    holder.statusChip.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.found_green));
                    holder.statusChip.setBackgroundResource(R.drawable.status_found_background);
                    break;
                case "COMPLETED":
                    holder.statusChip.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary));
                    holder.statusChip.setBackgroundResource(R.drawable.status_completed_background);
                    break;
                default:
                    holder.statusChip.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary));
                    holder.statusChip.setBackgroundResource(0);
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
            // Lỗi tiềm ẩn: Model Post không có imageResource. Sẽ cần sửa sau.
            // holder.imageView.setImageResource(post.getImageResource());
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updateData(List<Post> newPosts) {
        this.posts.clear();
        this.posts.addAll(newPosts);
        notifyDataSetChanged();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView dateTextView;
        public com.google.android.material.textview.MaterialTextView statusChip;
        public ImageView imageView;
        public MaterialButton editButton, deleteButton, completeButton;

        @SuppressLint("WrongViewCast")
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.itemTitleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            statusChip = itemView.findViewById(R.id.statusTextView);
            imageView = itemView.findViewById(R.id.itemImageView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            completeButton = itemView.findViewById(R.id.completeButton);
        }
    }
} 