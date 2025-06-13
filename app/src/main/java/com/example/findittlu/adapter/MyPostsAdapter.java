package com.example.findittlu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findittlu.R;
import com.example.findittlu.databinding.ItemMyPostBinding;
import com.example.findittlu.model.Post;

import java.util.List;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context context;

    public MyPostsAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    public void updateData(List<Post> newPostList) {
        this.postList.clear();
        this.postList.addAll(newPostList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view cho mỗi item từ layout item_my_post.xml
        ItemMyPostBinding binding = ItemMyPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        // Lấy dữ liệu của item tại vị trí 'position'
        Post post = postList.get(position);
        // Gắn dữ liệu vào các view trong ViewHolder
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        private final ItemMyPostBinding binding;

        public PostViewHolder(ItemMyPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Phương thức để gắn dữ liệu vào view
        public void bind(Post post) {
            binding.itemTitleTextView.setText(post.getTitle());
            binding.dateTextView.setText(post.getDate());
            binding.statusTextView.setText(post.getStatusText());

            // Xử lý logic hiển thị dựa trên trạng thái của bài đăng
            switch (post.getStatusType()) {
                case "SEARCHING":
                    binding.statusTextView.setBackgroundResource(R.drawable.status_searching_background);
                    binding.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.lost_red));
                    setButtonsVisibility(true, true);
                    break;
                case "FOUND":
                    binding.statusTextView.setBackgroundResource(R.drawable.status_holding_background);
                    binding.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.primary_blue));
                    setButtonsVisibility(true, true);
                    break;
                case "COMPLETED":
                    binding.statusTextView.setBackgroundResource(R.drawable.status_completed_background);
                    binding.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.found_green));
                    // Khi đã xong, chỉ hiện nút Xóa
                    setButtonsVisibility(false, false);
                    break;
            }
        }

        private void setButtonsVisibility(boolean showEdit, boolean showComplete) {
            binding.editButton.setVisibility(showEdit ? View.VISIBLE : View.GONE);
            binding.completeButton.setVisibility(showComplete ? View.VISIBLE : View.GONE);
        }
    }
}

