package com.example.findittlu.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

public class KeyboardUtils {

    public interface KeyboardVisibilityListener {
        void onKeyboardVisibilityChanged(boolean isVisible);
    }

    public static void addKeyboardVisibilityListener(Activity activity, KeyboardVisibilityListener listener) {
        final View rootView = activity.findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean wasVisible = false;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                boolean isVisible = keypadHeight > screenHeight * 0.15;

                if (isVisible != wasVisible) {
                    listener.onKeyboardVisibilityChanged(isVisible);
                }
                wasVisible = isVisible;
            }
        });
    }
} 