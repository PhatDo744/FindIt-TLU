package com.example.findittlu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findittlu.R;
import com.example.findittlu.adapter.SearchResultAdapter;
import com.example.findittlu.adapter.SearchResultItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.button.MaterialButton;
import android.content.res.ColorStateList;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView searchResultRecyclerView;
    private SearchResultAdapter searchResultAdapter;
    private List<SearchResultItem> searchResultList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupBottomNavigation();
        searchResultRecyclerView = findViewById(R.id.searchResultRecyclerView);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tạo dữ liệu mẫu
        searchResultList = new ArrayList<>();
        searchResultList.add(new SearchResultItem(R.drawable.image_placeholder_background, true, "Ví da màu đen hiệu Gucci", "Ví/Túi xách", "Thư viện T45", "15/05/2025"));
        searchResultList.add(new SearchResultItem(R.drawable.image_placeholder_background, false, "Thẻ sinh viên Hoàng Tiến Phúc", "Đồ điện tử", "Sân bóng TLU", "16/05/2025"));
        searchResultList.add(new SearchResultItem(R.drawable.image_placeholder_background, true, "Máy tính laptop", "Đồ điện tử", "Kí túc xá K1", "16/05/2025"));

        searchResultAdapter = new SearchResultAdapter(searchResultList);
        searchResultRecyclerView.setAdapter(searchResultAdapter);

        setupFilterButtons();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_search);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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
            } else if (itemId == R.id.navigation_search) {
                // Đã ở trang tìm kiếm, không làm gì cả
                return true;
            } else if (itemId == R.id.navigation_notifications) {
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void setupFilterButtons() {
        // Trạng thái
        MaterialButton btnStatusAll = findViewById(R.id.btnStatusAll);
        MaterialButton btnStatusLost = findViewById(R.id.btnStatusLost);
        MaterialButton btnStatusFound = findViewById(R.id.btnStatusFound);
        MaterialButton[] statusButtons = {btnStatusAll, btnStatusLost, btnStatusFound};

        // Danh mục
        MaterialButton btnCategoryAll = findViewById(R.id.btnCategoryAll);
        MaterialButton btnCategoryElectronics = findViewById(R.id.btnCategoryElectronics);
        MaterialButton btnCategoryPaper = findViewById(R.id.btnCategoryPaper);
        MaterialButton btnCategoryWallet = findViewById(R.id.btnCategoryWallet);
        MaterialButton[] categoryButtons = {btnCategoryAll, btnCategoryElectronics, btnCategoryPaper, btnCategoryWallet};

        // Mặc định chọn "Tất cả" trạng thái và danh mục
        selectButton(btnStatusAll, statusButtons);
        selectButton(btnCategoryAll, categoryButtons);

        btnStatusAll.setOnClickListener(v -> selectButton(btnStatusAll, statusButtons));
        btnStatusLost.setOnClickListener(v -> selectButton(btnStatusLost, statusButtons));
        btnStatusFound.setOnClickListener(v -> selectButton(btnStatusFound, statusButtons));

        btnCategoryAll.setOnClickListener(v -> selectButton(btnCategoryAll, categoryButtons));
        btnCategoryElectronics.setOnClickListener(v -> selectButton(btnCategoryElectronics, categoryButtons));
        btnCategoryPaper.setOnClickListener(v -> selectButton(btnCategoryPaper, categoryButtons));
        btnCategoryWallet.setOnClickListener(v -> selectButton(btnCategoryWallet, categoryButtons));
    }

    private void selectButton(MaterialButton selected, MaterialButton[] group) {
        int blue = getResources().getColor(R.color.primary_blue);
        int white = getResources().getColor(R.color.white);
        int gray = getResources().getColor(R.color.text_secondary);
        for (MaterialButton btn : group) {
            if (btn == selected) {
                btn.setBackgroundTintList(ColorStateList.valueOf(blue));
                btn.setTextColor(white);
            } else {
                btn.setBackgroundTintList(ColorStateList.valueOf(white));
                btn.setTextColor(gray);
            }
        }
    }
} 