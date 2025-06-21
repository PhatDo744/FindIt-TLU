package com.example.findittlu.utils;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import android.widget.ImageView;

public class ImageUtils {
    
    private static final String BASE_URL_EMULATOR = "http://10.0.2.2:8000";
//    private static final String BASE_URL_DEVICE = "http://192.168.1.5:8000"; // Thay đổi IP theo mạng của Phát
    private static final String BASE_URL_DEVICE = "http://192.168.1.4:8000"; // Thay đổi IP theo mạng của Quang
    
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
        // Nếu là đường dẫn bắt đầu bằng / thì nối BASE_URL_DEVICE
        if (imageUrl.startsWith("/")) {
            return BASE_URL_DEVICE + imageUrl;
        }
        // Trường hợp khác, thêm dấu / nếu thiếu
        return BASE_URL_DEVICE + "/" + imageUrl;
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
        String fullUrl = getFullImageUrl(imageUrl);
        if (fullUrl != null) {
            Glide.with(context)
                .load(fullUrl)
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(com.example.findittlu.R.drawable.ic_person)
                .error(com.example.findittlu.R.drawable.ic_person)
                .into(imageView);
        } else {
            imageView.setImageResource(com.example.findittlu.R.drawable.ic_person);
        }
    }
    
    /**
     * Load ảnh item với placeholder mặc định
     */
    public static void loadItemImage(Context context, String imageUrl, ImageView imageView) {
        loadImage(context, imageUrl, imageView, com.example.findittlu.R.drawable.image_placeholder_background);
    }
} 