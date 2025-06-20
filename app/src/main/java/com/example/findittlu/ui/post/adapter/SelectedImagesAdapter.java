package com.example.findittlu.ui.post.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.findittlu.R;
import com.example.findittlu.data.model.ItemImage;
import com.example.findittlu.utils.ImageUtils;

import java.util.List;
import java.util.Objects;

public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.SelectedImageViewHolder> {

    private List<Object> imageItems; // Can be Uri or ItemImage
    private OnImageRemoveListener removeListener;

    public interface OnImageRemoveListener {
        void onImageRemove(Object imageItem, int position);
    }

    public SelectedImagesAdapter(List<Object> imageItems, OnImageRemoveListener removeListener) {
        this.imageItems = imageItems;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public SelectedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_image, parent, false);
        return new SelectedImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImageViewHolder holder, int position) {
        Object imageItem = imageItems.get(position);

        String imageUrl = null;
        if (imageItem instanceof Uri) {
            imageUrl = imageItem.toString();
        } else if (imageItem instanceof ItemImage) {
            imageUrl = ImageUtils.getFullImageUrl(((ItemImage) imageItem).getImageUrl());
        }

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
            .load(imageUrl)
            .placeholder(R.drawable.image_placeholder_background)
            .error(R.drawable.image_placeholder_background)
            .centerCrop()
            .into(holder.imageView);
        
        // Setup remove button
        holder.removeButton.setOnClickListener(v -> {
            if (removeListener != null) {
                // We need to get the adapter position again in case it has changed
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    removeListener.onImageRemove(imageItems.get(currentPosition), currentPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    public static class SelectedImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView removeButton;

        public SelectedImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.selectedImageView);
            removeButton = itemView.findViewById(R.id.removeImageButton);
        }
    }
} 