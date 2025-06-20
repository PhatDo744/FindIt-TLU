package com.example.findittlu.ui.profile.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.findittlu.R;
import com.example.findittlu.data.model.Post;
import com.google.android.material.button.MaterialButton;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.findittlu.utils.ImageUtils;

import java.util.List;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.PostViewHolder> {

    private Context context;
    private List<Post> posts;
    private OnPostActionListener listener;

    public interface OnPostActionListener {
        void onCompleteClick(Post post);
        void onDeleteClick(Post post);
    }

    public MyPostsAdapter(Context context, List<Post> posts, OnPostActionListener listener) {
        this.context = context;
        this.posts = posts;
        this.listener = listener;
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

            holder.editButton.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("postId", String.valueOf(post.getId()));
                Navigation.findNavController(v).navigate(R.id.action_myPostsFragment_to_editPostFragment, bundle);
            });

            holder.completeButton.setOnClickListener(v -> {
                if (listener != null) {
                    new AlertDialog.Builder(context)
                            .setTitle("Xác nhận hoàn thành")
                            .setMessage("Bạn có chắc chắn muốn đánh dấu tin '" + post.getTitle() + "' là đã xong không?")
                            .setPositiveButton("Xác nhận", (dialog, which) -> listener.onCompleteClick(post))
                            .setNegativeButton("Hủy", null)
                            .show();
                }
            });

            holder.deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    // Create a new AlertDialog Builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.dialog_confirm_delete, null);
                    builder.setView(dialogView);

                    final AlertDialog dialog = builder.create();

                    // Set onClick listeners for the custom buttons
                    MaterialButton deleteButton = dialogView.findViewById(R.id.buttonDelete);
                    MaterialButton cancelButton = dialogView.findViewById(R.id.buttonCancel);

                    deleteButton.setOnClickListener(view -> {
                        listener.onDeleteClick(post);
                        dialog.dismiss();
                    });

                    cancelButton.setOnClickListener(view -> dialog.dismiss());
                    
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }
                    
                    dialog.show();
                }
            });

            // Hiển thị cảnh báo nếu sắp hết hạn
            if (post.isExpiringSoon()) {
                holder.expiryWarningTextView.setVisibility(View.VISIBLE);
                holder.expiryWarningTextView.setText("⚠️ Sắp hết hạn (còn " + post.getDaysUntilExpiration() + " ngày)");
                holder.expiryWarningTextView.setTextColor(ContextCompat.getColor(context, R.color.warning_orange));
            } else {
                holder.expiryWarningTextView.setVisibility(View.GONE);
            }

            // Hiển thị nút theo trạng thái
            String status = post.getStatus().toLowerCase();
            switch (status) {
                case "returned":
                case "completed":
                    // Tin đã xong: chỉ có nút Xóa
                    holder.editButton.setVisibility(View.GONE);
                    holder.completeButton.setVisibility(View.GONE);
                    holder.deleteButton.setVisibility(View.VISIBLE);
                    break;
                case "approved":
                    // Tin đã duyệt: có cả 3 nút Sửa, Xóa, Đã xong
                    holder.editButton.setVisibility(View.VISIBLE);
                    holder.completeButton.setVisibility(View.VISIBLE);
                    holder.deleteButton.setVisibility(View.VISIBLE);
                    break;
                default:
                    // Các trạng thái khác (pending, rejected, expired...): có nút Sửa, Xóa
                    holder.editButton.setVisibility(View.VISIBLE);
                    holder.completeButton.setVisibility(View.GONE);
                    holder.deleteButton.setVisibility(View.VISIBLE);
                    break;
            }
            
            // Xử lý hình ảnh nếu có
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                // Load hình ảnh đầu tiên nếu có
                String imageUrl = post.getImages().get(0).getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    ImageUtils.loadItemImage(holder.imageView.getContext(), imageUrl, holder.imageView);
                } else {
                    holder.imageView.setImageResource(com.example.findittlu.R.drawable.image_placeholder_background);
                }
                holder.imageView.setVisibility(View.VISIBLE);
            } else {
                holder.imageView.setImageResource(com.example.findittlu.R.drawable.image_placeholder_background);
                holder.imageView.setVisibility(View.VISIBLE);
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