package com.example.findittlu.utils;

/**
 * Utility class để xử lý URL
 */
public class UrlUtils {
    
    /**
     * Chuyển đổi URL tương đối thành URL tuyệt đối
     * @param relativeUrl URL tương đối (ví dụ: /storage/images/photo.jpg)
     * @return URL tuyệt đối
     */
    public static String toAbsoluteUrl(String relativeUrl) {
        if (relativeUrl == null || relativeUrl.isEmpty()) {
            return null;
        }
        
        // Nếu đã là URL tuyệt đối, trả về nguyên bản
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        }
        
        // Thêm BASE_URL vào đầu
        String baseUrl = Constants.getStorageBaseUrl();
        if (relativeUrl.startsWith("/")) {
            return baseUrl + relativeUrl;
        } else {
            return baseUrl + "/" + relativeUrl;
        }
    }
    
    /**
     * Chuyển đổi URL avatar thành URL tuyệt đối
     * @param avatarUrl URL avatar
     * @return URL tuyệt đối
     */
    public static String toAbsoluteAvatarUrl(String avatarUrl) {
        return toAbsoluteUrl(avatarUrl);
    }
    
    /**
     * Chuyển đổi URL ảnh sản phẩm thành URL tuyệt đối
     * @param imageUrl URL ảnh sản phẩm
     * @return URL tuyệt đối
     */
    public static String toAbsoluteImageUrl(String imageUrl) {
        return toAbsoluteUrl(imageUrl);
    }
    
    /**
     * Kiểm tra xem URL có hợp lệ không
     * @param url URL cần kiểm tra
     * @return true nếu URL hợp lệ
     */
    public static boolean isValidUrl(String url) {
        return url != null && !url.isEmpty() && 
               (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/"));
    }
    
    /**
     * Lấy tên file từ URL
     * @param url URL
     * @return Tên file
     */
    public static String getFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1) {
            return url.substring(lastSlashIndex + 1);
        }
        
        return url;
    }
} 