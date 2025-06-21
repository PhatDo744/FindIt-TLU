package com.example.findittlu.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.findittlu.R;

public class CustomToast {
    public static void showCustomToast(Context context, String title, String message) {
        showCustomToast(context, title, message, false);
    }

    public static void showCustomToast(Context context, String title, String message, boolean isError) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);
        TextView titleView = layout.findViewById(R.id.toast_title);
        TextView messageView = layout.findViewById(R.id.toast_message);
        titleView.setText(title);
        messageView.setText(message);
        View root = layout.findViewById(R.id.toast_layout_root);
        android.widget.ImageView icon = layout.findViewById(R.id.toast_icon);
        if (isError) {
            icon.setImageResource(R.drawable.ic_x_circle);
            root.setBackgroundResource(R.drawable.bg_toast_error);
        } else {
            icon.setImageResource(R.drawable.ic_check_circle);
            root.setBackgroundResource(R.drawable.bg_toast_success);
        }
        Toast toast = new Toast(context.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        int yOffset = (int) (72 * context.getResources().getDisplayMetrics().density); // 72dp to px
        toast.setGravity(android.view.Gravity.TOP | android.view.Gravity.FILL_HORIZONTAL, 0, yOffset);
        toast.show();
    }
} 