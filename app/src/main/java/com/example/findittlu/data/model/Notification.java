package com.example.findittlu.data.model;

import java.util.Date;

public class Notification {
    private String id; // UUID
    private String type;
    private String notifiableType;
    private long notifiableId;
    private String data;
    private Date readAt;
    private long itemId;
    private Date createdAt;
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
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
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
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
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
        return data;
    }
} 