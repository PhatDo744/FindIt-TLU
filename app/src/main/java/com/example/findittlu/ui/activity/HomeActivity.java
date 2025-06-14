package com.example.findittlu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findittlu.R;
import com.example.findittlu.adapter.LostFoundItem;
import com.example.findittlu.adapter.LostFoundItemAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.widget.LinearLayout;
import com.google.android.material.button.MaterialButton;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recentItemsRecyclerView;
    private LostFoundItemAdapter lostFoundItemAdapter;
    private List<LostFoundItem> lostFoundItemList;
    private LinearLayout categoryLayout;
    private String selectedCategory = "Tất cả";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupToolbar();
        setupBottomNavigation();
        setupRecyclerView();
        setupCategoryButtons();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Đã ở trang chủ, không làm gì cả
                return true;
            } else if (itemId == R.id.navigation_post) {
                startActivity(new Intent(getApplicationContext(), PostItemActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_notifications) {
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        recentItemsRecyclerView = findViewById(R.id.recentItemsRecyclerView);
        recentItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        lostFoundItemList = new ArrayList<>();
        // Thêm dữ liệu mẫu
        lostFoundItemList.add(new LostFoundItem("Ví da màu đen hiệu Gucci", "Thư viện T45", "15/05/2025", true, R.drawable.image_placeholder_background));
        lostFoundItemList.add(new LostFoundItem("Thẻ sinh viên Hoàng Tiến Phúc", "Sân bóng TLU", "16/05/2025", false, R.drawable.image_placeholder_background));
        lostFoundItemList.add(new LostFoundItem("Laptop Dell XPS 15", "Phòng học A101", "14/05/2025", true, R.drawable.image_placeholder_background));
        // Thêm các mục khác nếu cần

        lostFoundItemAdapter = new LostFoundItemAdapter(this, lostFoundItemList);
        recentItemsRecyclerView.setAdapter(lostFoundItemAdapter);
    }

    private void setupCategoryButtons() {
        categoryLayout = findViewById(R.id.categoryLayout);
        int count = categoryLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = categoryLayout.getChildAt(i);
            if (v instanceof MaterialButton) {
                MaterialButton btn = (MaterialButton) v;
                btn.setOnClickListener(view -> {
                    // Đổi màu tất cả button về mặc định
                    for (int j = 0; j < count; j++) {
                        View v2 = categoryLayout.getChildAt(j);
                        if (v2 instanceof MaterialButton) {
                            MaterialButton btn2 = (MaterialButton) v2;
                            btn2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(android.R.color.transparent)));
                            btn2.setTextColor(getResources().getColor(R.color.text_secondary));
                        }
                    }
                    // Đổi màu button được chọn
                    btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_blue)));
                    btn.setTextColor(getResources().getColor(R.color.white));
                    selectedCategory = btn.getText().toString();
                    loadItemsByCategory(selectedCategory);
                });
            }
        }
        // Mặc định chọn "Tất cả"
        MaterialButton btnAll = (MaterialButton) categoryLayout.getChildAt(0);
        btnAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_blue)));
        btnAll.setTextColor(getResources().getColor(R.color.white));
    }

    private void loadItemsByCategory(String category) {
        // Dữ liệu mẫu, thực tế sẽ lấy từ DB hoặc API
        List<LostFoundItem> filteredList = new ArrayList<>();
        for (LostFoundItem item : lostFoundItemList) {
            if (category.equals("Tất cả") || item.getTitle().toLowerCase().contains(category.toLowerCase()) || item.getLocation().toLowerCase().contains(category.toLowerCase())) {
                filteredList.add(item);
            }
        }
        lostFoundItemAdapter = new LostFoundItemAdapter(this, filteredList);
        recentItemsRecyclerView.setAdapter(lostFoundItemAdapter);
    }
} 