package com.example.findittlu.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.findittlu.R;
import com.example.findittlu.adapter.LostFoundItem;
import com.example.findittlu.adapter.LostFoundItemAdapter;
import com.google.android.material.button.MaterialButton;
import android.widget.LinearLayout;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.ui.profile.adapter.MyPostsAdapter;
import com.example.findittlu.viewmodel.HomeViewModel;
import com.example.findittlu.viewmodel.CategoryViewModel;
import com.example.findittlu.data.model.Category;

import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ImageButton;

public class HomeFragment extends Fragment {
    private RecyclerView recentItemsRecyclerView;
    private LostFoundItemAdapter lostFoundItemAdapter;
    private List<LostFoundItem> lostFoundItemList;
    private LinearLayout categoryLayout;
    private String selectedCategory = "Tất cả";
    private HomeViewModel homeViewModel;
    private CategoryViewModel categoryViewModel;
    // Lưu map tên -> id danh mục để lọc
    private java.util.Map<String, Long> categoryNameToId = new java.util.HashMap<>();

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        recentItemsRecyclerView = view.findViewById(R.id.recentItemsRecyclerView);
        recentItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        lostFoundItemAdapter = new LostFoundItemAdapter(this, new ArrayList<>());
        recentItemsRecyclerView.setAdapter(lostFoundItemAdapter);
        // Gọi API lấy danh mục
        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            setupCategoryButtonsDynamic(view, categories);
        });
        // Observe API connection
        homeViewModel.getIsApiConnected().observe(getViewLifecycleOwner(), connected -> {
            if (!connected) Toast.makeText(getContext(), "Không thể kết nối API!", Toast.LENGTH_SHORT).show();
        });
        // Observe post list
        homeViewModel.getPostList().observe(getViewLifecycleOwner(), list -> {
            lostFoundItemAdapter = new LostFoundItemAdapter(this, list);
            recentItemsRecyclerView.setAdapter(lostFoundItemAdapter);
        });
        homeViewModel.fetchPosts();
        // Tìm kiếm theo từ khóa
        EditText searchEditText = view.findViewById(R.id.searchEditText);
        ImageButton searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            String keyword = searchEditText.getText().toString().trim();
            if (!keyword.isEmpty()) {
                homeViewModel.fetchPostsByKeyword(keyword);
            }
        });
    }

    // Hàm mới: tạo button danh mục động từ API
    private void setupCategoryButtonsDynamic(View view, java.util.List<Category> categories) {
        categoryLayout = view.findViewById(R.id.categoryLayout);
        categoryLayout.removeAllViews();
        categoryNameToId.clear();
        // Thêm nút "Tất cả"
        com.google.android.material.button.MaterialButton btnAll = new com.google.android.material.button.MaterialButton(requireContext(), null, R.style.CategoryButton);
        LinearLayout.LayoutParams paramsAll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsAll.setMarginEnd(16);
        btnAll.setLayoutParams(paramsAll);
        btnAll.setText("Tất cả");
        btnAll.setOnClickListener(v -> {
            selectCategoryButton(btnAll);
            selectedCategory = "Tất cả";
            homeViewModel.fetchPosts();
        });
        categoryLayout.addView(btnAll);
        // Thêm các button danh mục từ API
        if (categories != null) {
            for (Category cat : categories) {
                categoryNameToId.put(cat.getName(), cat.getId());
                com.google.android.material.button.MaterialButton btn = new com.google.android.material.button.MaterialButton(requireContext(), null, R.style.CategoryButton);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMarginEnd(16);
                btn.setLayoutParams(params);
                btn.setText(cat.getName());
                btn.setOnClickListener(v -> {
                    selectCategoryButton(btn);
                    selectedCategory = cat.getName();
                    Long catId = categoryNameToId.get(selectedCategory);
                    if (catId != null) {
                        homeViewModel.fetchPostsByCategory(catId);
                    }
                });
                categoryLayout.addView(btn);
            }
        }
        // Mặc định chọn "Tất cả"
        selectCategoryButton(btnAll);
    }

    // Hàm chọn button danh mục (đổi màu)
    private void selectCategoryButton(com.google.android.material.button.MaterialButton selectedBtn) {
        int count = categoryLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = categoryLayout.getChildAt(i);
            if (v instanceof com.google.android.material.button.MaterialButton) {
                com.google.android.material.button.MaterialButton btn = (com.google.android.material.button.MaterialButton) v;
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(android.R.color.transparent)));
                btn.setTextColor(getResources().getColor(R.color.text_secondary));
            }
        }
        selectedBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_blue)));
        selectedBtn.setTextColor(getResources().getColor(R.color.white));
    }

    private void loadItemsByCategory(String category) {
        if (homeViewModel == null || homeViewModel.getPostList().getValue() == null) return;
        List<LostFoundItem> filteredList = new ArrayList<>();
        for (LostFoundItem item : homeViewModel.getPostList().getValue()) {
            if (category.equals("Tất cả") || item.getTitle().toLowerCase().contains(category.toLowerCase()) || item.getLocation().toLowerCase().contains(category.toLowerCase())) {
                filteredList.add(item);
            }
        }
        lostFoundItemAdapter = new LostFoundItemAdapter(this, filteredList);
        recentItemsRecyclerView.setAdapter(lostFoundItemAdapter);
    }
}