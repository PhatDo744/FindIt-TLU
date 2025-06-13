package com.example.findittlu.model;

public class Post {
    private String title;
    private String date;
    private String statusText; // Text hiển thị (e.g., "Đang tìm")
    private String statusType; // Loại để lọc (e.g., "SEARCHING")

    public Post(String title, String date, String statusText, String statusType) {
        this.title = title;
        this.date = date;
        this.statusText = statusText;
        this.statusType = statusType;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getStatusType() {
        return statusType;
    }
}

