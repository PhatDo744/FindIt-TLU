package com.example.findittlu.data.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Post {
    private long id;
    private long userId;
    private long categoryId;
    private String title;
    private String description;
    private String locationType; // 'exact' hoặc 'area'
    private String locationDescription;
    private String itemType; // 'lost' hoặc 'found'
    private String status; // 'searching', 'holding', 'found', 'completed'
    private Date dateLostOrFound;
    private Date expirationDate;
    private boolean isContactInfoPublic;
    private String adminComment;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    
    // Quan hệ
    private User user;
    private Category category;
    private List<ItemImage> images;
    
    // Constructors
    public Post() {}
    
    // Constructor cũ để giữ tương thích
    public Post(String title, String date, String status) {
        this.title = title;
        this.status = status;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            this.dateLostOrFound = sdf.parse(date);
        } catch (ParseException e) {
            this.dateLostOrFound = new Date();
        }
        
        // Set expiration date = created date + 14 ngày
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 14);
        this.expirationDate = cal.getTime();
        
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Getters và Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getUserId() {
        return userId;
    }
    
    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLocationType() {
        return locationType;
    }
    
    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }
    
    public String getLocationDescription() {
        return locationDescription;
    }
    
    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }
    
    public String getItemType() {
        return itemType;
    }
    
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getDateLostOrFound() {
        return dateLostOrFound;
    }
    
    public void setDateLostOrFound(Date dateLostOrFound) {
        this.dateLostOrFound = dateLostOrFound;
    }
    
    public Date getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public boolean isContactInfoPublic() {
        return isContactInfoPublic;
    }
    
    public void setContactInfoPublic(boolean contactInfoPublic) {
        isContactInfoPublic = contactInfoPublic;
    }
    
    public String getAdminComment() {
        return adminComment;
    }
    
    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
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
    
    public Date getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public List<ItemImage> getImages() {
        return images;
    }
    
    public void setImages(List<ItemImage> images) {
        this.images = images;
    }
    
    // Helper methods
    public String getDate() {
        // Phương thức cũ để giữ tương thích
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateLostOrFound != null ? sdf.format(dateLostOrFound) : "";
    }
    
    // Phương thức để lấy text hiển thị từ status
    public String getStatusText() {
        if (status == null) return "Không xác định";
        
        switch (status.toUpperCase()) {
            case "SEARCHING":
                return "Đang tìm";
            case "HOLDING":
                return "Đang giữ";
            case "FOUND":
                return "Đã tìm thấy";
            case "COMPLETED":
                return "Đã xong";
            default:
                return "Không xác định";
        }
    }
    
    // Kiểm tra bài đăng đã hết hạn chưa
    public boolean isExpired() {
        if (expirationDate == null) return false;
        return new Date().after(expirationDate);
    }
    
    // Kiểm tra bài đăng sắp hết hạn (trong vòng 1 ngày)
    public boolean isExpiringSoon() {
        if (expirationDate == null) return false;
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = cal.getTime();
        
        return expirationDate.before(tomorrow) && !isExpired();
    }
    
    // Lấy số ngày còn lại trước khi hết hạn
    public int getDaysUntilExpiration() {
        if (expirationDate == null || isExpired()) return 0;
        
        long diff = expirationDate.getTime() - new Date().getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }
}

