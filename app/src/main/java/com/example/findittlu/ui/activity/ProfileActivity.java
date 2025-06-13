package com.example.findittlu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.findittlu.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Thiết lập Toolbar
        setupToolbar();

        // Thiết lập Bottom Navigation
        setupBottomNavigation();

        // Thiết lập các mục có thể click
        setupMenuItems();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

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

    private void setupMenuItems() {
        TextView personalInfo = findViewById(R.id.personalInfoTextView);
        TextView myPosts = findViewById(R.id.myPostsTextView);
        TextView logout = findViewById(R.id.logoutTextView);
        TextView policy = findViewById(R.id.policyTextView);
        TextView help = findViewById(R.id.helpTextView);

        personalInfo.setOnClickListener(v -> {
            // Chuyển sang màn hình Chỉnh sửa thông tin cá nhân
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });
        myPosts.setOnClickListener(v -> {
            // Chuyển sang màn hình Tin đăng của tôi
            Intent intent = new Intent(ProfileActivity.this, MyPostsActivity.class);
            startActivity(intent);
        });
        policy.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, PolicyActivity.class);
            startActivity(intent);
        });
        help.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HelpActivity.class);
            startActivity(intent);
        });
        logout.setOnClickListener(v -> {
            // Xử lý logic đăng xuất ở đây
            Toast.makeText(this, "Đăng xuất", Toast.LENGTH_SHORT).show();
            // Ví dụ: chuyển về màn hình đăng nhập
            // Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(intent);
            // finish();
        });

        // Thêm OnClickListener cho các mục khác nếu cần
    }
}
