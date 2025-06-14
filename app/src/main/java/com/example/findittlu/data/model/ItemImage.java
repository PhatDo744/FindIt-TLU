package com.example.findittlu.data.model;

public class ItemImage {
    private long id;
    private long itemId;
    private String imageUrl;
    private String caption;
    private String createdAt;
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