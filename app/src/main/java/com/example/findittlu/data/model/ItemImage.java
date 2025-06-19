package com.example.findittlu.data.model;

import com.google.gson.annotations.SerializedName;

public class ItemImage {
    @SerializedName("id")
    private long id;
    @SerializedName("item_id")
    private long itemId;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("caption")
    private String caption;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    // Getter & Setter
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getItemId() { return itemId; }
    public void setItemId(long itemId) { this.itemId = itemId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
} 