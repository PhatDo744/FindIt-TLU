package com.example.findittlu.ui.profile;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.findittlu.R;
import com.example.findittlu.ui.common.adapter.MyPostsViewPagerAdapter;
import com.example.findittlu.databinding.ActivityMyPostsBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyPostsFragment extends Fragment {

    private ActivityMyPostsBinding binding;
    private MyPostsViewPagerAdapter viewPagerAdapter;

    public MyPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityMyPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        setupToolbar(navController);
        setupViewPagerAndTabs();
    }

    private void setupToolbar(NavController navController) {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
    }

    private void setupViewPagerAndTabs() {
        // MyPostsViewPagerAdapter cần được truyền một Fragment thay vì một Activity
        viewPagerAdapter = new MyPostsViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    TextView tabTextView = new TextView(requireContext());
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
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Tránh memory leak
    }
} 