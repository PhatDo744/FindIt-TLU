package com.example.findittlu.utils;

/**
 * Lớp chứa các hằng số toàn cục của ứng dụng
 */
public class Constants {
    
    // Các URL server cho các môi trường khác nhau
    public static final String BASE_URL_LOCAL_QUANG = "http://192.168.1.4:8000";
    public static final String BASE_URL_LOCAL_PHAT = "http://192.168.1.3:8000";
    public static final String BASE_URL_EMULATOR = "http://10.0.2.2:8000";
    public static final String BASE_URL_PRODUCTION = "https://findit-tlu.com";
    
    // Chọn môi trường hiện tại - thay đổi ở đây để chuyển môi trường
    public static final String CURRENT_ENVIRONMENT = "LOCAL_QUANG"; // LOCAL_QUANG, LOCAL_PHAT, EMULATOR, PRODUCTION
    
    /**
     * Lấy BASE_URL dựa trên môi trường hiện tại
     */
    public static String getBaseUrl() {
        switch (CURRENT_ENVIRONMENT) {
            case "LOCAL_QUANG":
                return BASE_URL_LOCAL_QUANG;
            case "LOCAL_PHAT":
                return BASE_URL_LOCAL_PHAT;
            case "EMULATOR":
                return BASE_URL_EMULATOR;
            case "PRODUCTION":
                return BASE_URL_PRODUCTION;
            default:
                return BASE_URL_LOCAL_PHAT; // Default fallback
        }
    }
    
    /**
     * Lấy BASE_URL cho API (có thêm /api/)
     */
    public static String getApiBaseUrl() {
        return getBaseUrl() + "/api/";
    }
    
    /**
     * Lấy BASE_URL cho storage (không có /api/)
     */
    public static String getStorageBaseUrl() {
        return getBaseUrl();
    }
    
    /**
     * Kiểm tra xem có phải môi trường development không
     */
    public static boolean isDevelopment() {
        return !CURRENT_ENVIRONMENT.equals("PRODUCTION");
    }
    
    /**
     * Kiểm tra xem có phải môi trường production không
     */
    public static boolean isProduction() {
        return CURRENT_ENVIRONMENT.equals("PRODUCTION");
    }
    
    /**
     * Lấy tên môi trường hiện tại
     */
    public static String getCurrentEnvironment() {
        return CURRENT_ENVIRONMENT;
    }
} 