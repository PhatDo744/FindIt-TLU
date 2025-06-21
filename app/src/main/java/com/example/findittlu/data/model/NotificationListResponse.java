package com.example.findittlu.data.model;
import java.util.List;

public class NotificationListResponse {
    private List<Notification> data;
    // Có thể thêm trường meta nếu cần

    public List<Notification> getData() { return data; }
    public void setData(List<Notification> data) { this.data = data; }
} 