package com.example.findittlu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findittlu.R;
import com.example.findittlu.adapter.NotificationItem;
import com.example.findittlu.adapter.NotificationsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Thiết lập Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Thiết lập RecyclerView
        RecyclerView recyclerView = findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<NotificationItem> notificationList = new ArrayList<>();
        notificationList.add(new NotificationItem(NotificationItem.TYPE_SUCCESS, "Tin đăng 'Mất thẻ sinh viên khu A2' của bạn đã được duyệt.", "5 phút trước"));
        notificationList.add(new NotificationItem(NotificationItem.TYPE_SUCCESS, "Tin 'Nhặt được tai nghe Bluetooth' đã được bạn đánh dấu hoàn thành.", "1 giờ trước"));
        notificationList.add(new NotificationItem(NotificationItem.TYPE_INFO, "Chào mừng bạn đến với FindIt@TLU! Hãy bắt đầu tìm kiếm hoặc đăng tin.", "Hôm qua"));
        notificationList.add(new NotificationItem(NotificationItem.TYPE_WARNING, "Tin 'Mất chia khóa phòng KTX' của bạn sắp hết hạn hiển thị. Hãy gia hạn nếu cần.", "3 ngày trước"));
        NotificationsAdapter adapter = new NotificationsAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        // Thiết lập Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_notifications);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_post) {
                startActivity(new Intent(getApplicationContext(), PostItemActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_notifications) {
                return true;
            }
            return false;
        });
    }
} 