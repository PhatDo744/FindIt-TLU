package com.example.findittlu.data.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.google.gson.annotations.SerializedName;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;

public class Post {
    @SerializedName("id")
    private long id = 0;
    @SerializedName("user_id")
    private long userId = 0;
    @SerializedName("category_id")
    private long categoryId;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("location_type")
    private String locationType; // 'exact' hoặc 'area'
    @SerializedName("location_description")
    private String locationDescription;
    @SerializedName("item_type")
    private String itemType; // 'lost' hoặc 'found'
    @SerializedName("status")
    private String status; // 'searching', 'holding', 'found', 'completed'
    @SerializedName("date_lost_or_found")
    private Date dateLostOrFound;
    @SerializedName("expiration_date")
    private Date expirationDate;
    @SerializedName("is_contact_info_public")
    private boolean isContactInfoPublic;
    @SerializedName("admin_comment")
    private String adminComment;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;
    @SerializedName("deleted_at")
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
        String type = itemType != null ? itemType.toLowerCase() : "";
        String st = status.toLowerCase();
        if ("returned".equals(st) || "completed".equals(st)) {
            return "Đã xong";
        }
        if ("lost".equals(type)) {
            // Tin mất: các trạng thái duyệt đều là Đang tìm
            if ("approved".equals(st) || "pending_approval".equals(st) || "rejected".equals(st) || "expired".equals(st)) {
                return "Đang tìm";
            }
        } else if ("found".equals(type)) {
            // Tin nhặt: các trạng thái duyệt đều là Đang giữ
            if ("approved".equals(st) || "pending_approval".equals(st) || "rejected".equals(st) || "expired".equals(st)) {
                return "Đang giữ";
            }
        }
        // Fallback cho các trạng thái khác
        switch (st) {
            case "searching":
                return "Đang tìm";
            case "holding":
                return "Đang giữ";
            case "found":
                return "Đã tìm thấy";
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

    // Custom serializer để không gửi id và userId khi chúng bằng 0
    public static class PostSerializer implements JsonSerializer<Post> {
        @Override
        public JsonElement serialize(Post post, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            
            // Chỉ thêm id và userId nếu chúng khác 0
            if (post.id != 0) {
                jsonObject.addProperty("id", post.id);
            }
            if (post.userId != 0) {
                jsonObject.addProperty("user_id", post.userId);
            }
            
            // Thêm các trường khác
            if (post.categoryId != 0) {
                jsonObject.addProperty("category_id", post.categoryId);
            }
            if (post.title != null) {
                jsonObject.addProperty("title", post.title);
            }
            if (post.description != null) {
                jsonObject.addProperty("description", post.description);
            }
            if (post.locationDescription != null) {
                jsonObject.addProperty("location_description", post.locationDescription);
            }
            if (post.itemType != null) {
                jsonObject.addProperty("item_type", post.itemType);
            }
            if (post.status != null) {
                jsonObject.addProperty("status", post.status);
            }
            if (post.dateLostOrFound != null) {
                // Sử dụng Calendar để đảm bảo ngày không bị ảnh hưởng timezone
                Calendar cal = Calendar.getInstance();
                cal.setTime(post.dateLostOrFound);
                cal.set(Calendar.HOUR_OF_DAY, 12); // Set giờ về 12:00
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); // Sử dụng UTC để tránh timezone
                String formattedDate = sdf.format(cal.getTime());
                
                android.util.Log.d("PostSerializer", "Original date: " + post.dateLostOrFound);
                android.util.Log.d("PostSerializer", "Calendar date: " + cal.getTime());
                android.util.Log.d("PostSerializer", "Formatted date: " + formattedDate);
                
                jsonObject.addProperty("date_lost_or_found", formattedDate);
            }
            jsonObject.addProperty("is_contact_info_public", post.isContactInfoPublic);
            
            return jsonObject;
        }
    }
}

