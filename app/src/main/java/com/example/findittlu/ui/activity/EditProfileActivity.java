package com.example.findittlu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.findittlu.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Thiết lập Toolbar
        setupToolbar();

        // Thiết lập Bottom Navigation
        setupBottomNavigation();

        // Thiết lập các nút
        setupButtons();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    // ================== PHẦN ĐƯỢC CẬP NHẬT ==================
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Đánh dấu mục hiện tại là "Cá nhân"
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);

        // bên trong hàm setupBottomNavigation của EditProfileActivity.java

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId != R.id.navigation_profile) { // Nếu nhấn vào bất kỳ mục nào khác ngoài "Cá nhân"
                // Chuyển sang ProfileActivity và đánh dấu mục được chọn
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                // Bạn có thể truyền ID của mục được chọn để ProfileActivity xử lý tiếp
                // intent.putExtra("selected_item_id", itemId);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return true; // Đã ở màn hình cá nhân, không làm gì
        });
    }
    // =======================================================

    private void setupButtons() {
        MaterialButton saveButton = findViewById(R.id.saveButton);
        MaterialButton cancelButton = findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> {
            Toast.makeText(this, "Lưu thay đổi", Toast.LENGTH_SHORT).show();
        });

        cancelButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}
