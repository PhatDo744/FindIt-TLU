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

import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Toast;

public class HomeFragment extends Fragment {
    private RecyclerView recentItemsRecyclerView;
    private LostFoundItemAdapter lostFoundItemAdapter;
    private List<LostFoundItem> lostFoundItemList;
    private LinearLayout categoryLayout;
    private String selectedCategory = "Tất cả";
    private HomeViewModel homeViewModel;

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
        recentItemsRecyclerView = view.findViewById(R.id.recentItemsRecyclerView);
        recentItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        lostFoundItemAdapter = new LostFoundItemAdapter(this, new ArrayList<>());
        recentItemsRecyclerView.setAdapter(lostFoundItemAdapter);
        setupCategoryButtons(view);
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
    }

    private void setupCategoryButtons(View view) {
        categoryLayout = view.findViewById(R.id.categoryLayout);
        int count = categoryLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = categoryLayout.getChildAt(i);
            if (v instanceof MaterialButton) {
                MaterialButton btn = (MaterialButton) v;
                btn.setOnClickListener(view1 -> {
                    for (int j = 0; j < count; j++) {
                        View v2 = categoryLayout.getChildAt(j);
                        if (v2 instanceof MaterialButton) {
                            MaterialButton btn2 = (MaterialButton) v2;
                            btn2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getResources().getColor(android.R.color.transparent)));
                            btn2.setTextColor(requireContext().getResources().getColor(R.color.text_secondary));
                        }
                    }
                    btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getResources().getColor(R.color.primary_blue)));
                    btn.setTextColor(requireContext().getResources().getColor(R.color.white));
                    selectedCategory = btn.getText().toString();
                    loadItemsByCategory(selectedCategory);
                });
            }
        }
        MaterialButton btnAll = (MaterialButton) categoryLayout.getChildAt(0);
        btnAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getResources().getColor(R.color.primary_blue)));
        btnAll.setTextColor(requireContext().getResources().getColor(R.color.white));
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