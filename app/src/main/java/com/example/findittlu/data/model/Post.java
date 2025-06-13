package com.example.findittlu.data.model;

public class Post {
    private String title;
    private String date;
    private String status; // Chỉ cần một trường status, ví dụ: "SEARCHING", "FOUND", "COMPLETED"
    private long id;
    private long userId;
    private long categoryId;
    private String description;
    private String locationDescription;
    private String itemType; // lost/found
    private String dateLostOrFound;
    private boolean isContactInfoPublic;
    private String expirationDate;
    private String adminComment;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private java.util.List<ItemImage> images;

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

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getDescription() {
        return description;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getItemType() {
        return itemType;
    }

    public String getDateLostOrFound() {
        return dateLostOrFound;
    }

    public boolean isContactInfoPublic() {
        return isContactInfoPublic;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public java.util.List<ItemImage> getImages() {
        return images;
    }
}

