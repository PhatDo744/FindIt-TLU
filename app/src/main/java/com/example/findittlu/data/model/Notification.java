package com.example.findittlu.data.model;

import java.util.Date;
import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("id")
    private String id; // UUID
    @SerializedName("type")
    private String type;
    @SerializedName("notifiable_type")
    private String notifiableType;
    @SerializedName("notifiable_id")
    private long notifiableId;
    @SerializedName("data")
    private Data data;
    @SerializedName("read_at")
    private Date readAt;
    @SerializedName("item_id")
    private long itemId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;
    
    // Quan hệ
    private Post item;
    
    // Constructors
    public Notification() {}
    
    // Getters và Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getNotifiableType() {
        return notifiableType;
    }
    
    public void setNotifiableType(String notifiableType) {
        this.notifiableType = notifiableType;
    }
    
    public long getNotifiableId() {
        return notifiableId;
    }
    
    public void setNotifiableId(long notifiableId) {
        this.notifiableId = notifiableId;
    }
    
    public Data getData() {
        return data;
    }
    
    public void setData(Data data) {
        this.data = data;
    }
    
    public Date getReadAt() {
        return readAt;
    }
    
    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }
    
    public long getItemId() {
        return itemId;
    }
    
    public void setItemId(long itemId) {
        this.itemId = itemId;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Post getItem() {
        return item;
    }
    
    public void setItem(Post item) {
        this.item = item;
    }
    
    // Helper methods
    public boolean isRead() {
        return readAt != null;
    }
    
    public String getNotificationMessage() {
        // Parse data JSON để lấy message
        // TODO: Implement JSON parsing
        return data.getTitle();
    }

    public static class Data {
        @SerializedName("title")
        private String title;
        @SerializedName("item_id")
        private int item_id;
        @SerializedName("type")
        private String type;
        @SerializedName("reason")
        private String reason;
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public int getItem_id() {
            return item_id;
        }
        
        public void setItem_id(int item_id) {
            this.item_id = item_id;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
    }
} 