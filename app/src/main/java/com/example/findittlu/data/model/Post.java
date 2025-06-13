package com.example.findittlu.data.model;

public class Post {
    private String title;
    private String date;
    private String status; // Chỉ cần một trường status, ví dụ: "SEARCHING", "FOUND", "COMPLETED"

    public Post(String title, String date, String status) {
        this.title = title;
        this.date = date;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    // Phương thức để lấy text hiển thị từ status
    public String getStatusText() {
        switch (status.toUpperCase()) {
            case "SEARCHING":
                return "Đang tìm";
            case "FOUND":
                return "Đang giữ";
            case "COMPLETED":
                return "Đã xong";
            default:
                return "Không xác định";
        }
    }
}

