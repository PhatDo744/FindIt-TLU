package com.example.findittlu.ui.activity; // Thay thế bằng package name của bạn

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findittlu.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PolicyActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đặt layout cho Activity. Tất cả nội dung đã được định nghĩa trong XML.
        setContentView(R.layout.activity_policy);

        // Ánh xạ các view
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Thiết lập Toolbar
        setupToolbar();

        // Thiết lập Bottom Navigation
        setupBottomNavigation();
    }

    /**
     * Cấu hình Toolbar với nút quay lại.
     */
    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Cấu hình BottomNavigationView.
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_post) {
                startActivity(new Intent(getApplicationContext(), PostItemActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            // Thêm điều hướng cho các mục khác nếu cần
            // else if (itemId == R.id.navigation_home) { ... }

            return itemId == R.id.navigation_profile;
        });
    }
}
