package com.example.findittlu.utils;

import android.content.Context;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;

/**
 * Utility class để hiển thị Snackbar một cách an toàn trong Fragment
 * Tránh crash khi Fragment chưa được attach hoặc view chưa sẵn sàng
 */
public class SafeSnackbar {
    
    /**
     * Hiển thị Snackbar an toàn trong Fragment
     * @param fragment Fragment cần hiển thị Snackbar
     * @param message Nội dung thông báo
     * @param duration Thời gian hiển thị
     */
    public static void show(Fragment fragment, String message, int duration) {
        if (fragment != null && fragment.isAdded() && fragment.getActivity() != null) {
            Snackbar.make(fragment.getActivity().findViewById(android.R.id.content), message, duration).show();
        } else if (fragment != null && fragment.getContext() != null) {
            Toast.makeText(fragment.getContext(), message, duration == Snackbar.LENGTH_LONG ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Hiển thị Snackbar an toàn với thời gian ngắn
     * @param fragment Fragment cần hiển thị Snackbar
     * @param message Nội dung thông báo
     */
    public static void showShort(Fragment fragment, String message) {
        show(fragment, message, Snackbar.LENGTH_SHORT);
    }
    
    /**
     * Hiển thị Snackbar an toàn với thời gian dài
     * @param fragment Fragment cần hiển thị Snackbar
     * @param message Nội dung thông báo
     */
    public static void showLong(Fragment fragment, String message) {
        show(fragment, message, Snackbar.LENGTH_LONG);
    }
    
    /**
     * Hiển thị Snackbar an toàn với Context
     * @param context Context
     * @param message Nội dung thông báo
     * @param duration Thời gian hiển thị
     */
    public static void show(Context context, String message, int duration) {
        if (context != null) {
            Toast.makeText(context, message, duration == Snackbar.LENGTH_LONG ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }
} 