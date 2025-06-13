package com.example.findittlu.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.findittlu.R;
import com.example.findittlu.adapter.MyPostsViewPagerAdapter;
import com.example.findittlu.databinding.ActivityMyPostsBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyPostsActivity extends AppCompatActivity {

    private ActivityMyPostsBinding binding;
    private MyPostsViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập các thành phần giao diện
        setupToolbar();
        setupViewPagerAndTabs();
        setupBottomNavigation();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupViewPagerAndTabs() {
        viewPagerAdapter = new MyPostsViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    TextView tabTextView = new TextView(this);
                    tabTextView.setGravity(Gravity.CENTER);
                    switch (position) {
                        case 0: tabTextView.setText("Tất cả"); break;
                        case 1: tabTextView.setText("Tìm kiếm"); break;
                        case 2: tabTextView.setText("Tìm thấy"); break;
                        case 3: tabTextView.setText("Đã xong"); break;
                    }

                    tab.setCustomView(tabTextView);
                }
        ).attach();
    }

    private void setupBottomNavigation() {
        // Đánh dấu mục "Cá nhân" là đang được chọn vì màn hình này thuộc luồng cá nhân
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_profile);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // TODO: Thay thế bằng Intent để mở HomeActivity
                Toast.makeText(this, "Chuyển đến Trang chủ", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                // overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_search) {
                // TODO: Thay thế bằng Intent để mở SearchActivity
                Toast.makeText(this, "Chuyển đến Tìm kiếm", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                // overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_post) {
                startActivity(new Intent(getApplicationContext(), PostItemActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_notifications) {
                // TODO: Thay thế bằng Intent để mở NotificationsActivity
                Toast.makeText(this, "Chuyển đến Thông báo", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                // overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // Chuyển về màn hình Profile chính khi nhấn vào mục này.
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}
