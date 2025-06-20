package com.example.findittlu.utils;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import android.widget.ImageView;

public class ImageUtils {
    
    private static final String BASE_URL_EMULATOR = "http://10.0.2.2:8000";
    private static final String BASE_URL_DEVICE = "http://192.168.1.5:8000"; // Thay đổi IP theo mạng của bạn
    
    /**
     * Chuyển đổi URL tương đối thành URL đầy đủ
     */
    public static String getFullImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        // Nếu đã là URL đầy đủ thì trả về nguyên bản
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }
        
        // Thêm dấu / nếu imageUrl không bắt đầu bằng /
        if (!imageUrl.startsWith("/")) {
            imageUrl = "/" + imageUrl;
        }
        
        // Thêm base URL cho device thật (có thể thay đổi cho emulator)
        return BASE_URL_DEVICE + imageUrl;
    }
    
    /**
     * Load ảnh với Glide với placeholder và error handling
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView, int placeholderResId, int errorResId) {
        String fullUrl = getFullImageUrl(imageUrl);
        
        if (fullUrl != null) {
            Glide.with(context)
                .load(fullUrl)
                .apply(new RequestOptions()
                    .placeholder(placeholderResId)
                    .error(errorResId))
                .into(imageView);
        } else {
            imageView.setImageResource(placeholderResId);
        }
    }
    
    /**
     * Load ảnh với placeholder mặc định
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView, int placeholderResId) {
        loadImage(context, imageUrl, imageView, placeholderResId, placeholderResId);
    }
    
    /**
     * Load avatar với placeholder mặc định là ic_person
     */
    public static void loadAvatar(Context context, String imageUrl, ImageView imageView) {
        loadImage(context, imageUrl, imageView, com.example.findittlu.R.drawable.ic_person);
    }
    
    /**
     * Load ảnh item với placeholder mặc định
     */
    public static void loadItemImage(Context context, String imageUrl, ImageView imageView) {
        loadImage(context, imageUrl, imageView, com.example.findittlu.R.drawable.image_placeholder_background);
    }
} 