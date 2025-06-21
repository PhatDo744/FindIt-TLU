package com.example.findittlu.ui.profile;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.findittlu.ui.profile.adapter.MyPostsViewPagerAdapter;
import com.example.findittlu.databinding.FragmentMyPostsBinding;  // ✅ ĐÃ SỬA
import com.example.findittlu.viewmodel.MyPostsViewModel;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyPostsFragment extends Fragment {

    private FragmentMyPostsBinding binding;  // ✅ ĐÃ SỬA
    private MyPostsViewPagerAdapter viewPagerAdapter;
    private MyPostsViewModel viewModel;

    public MyPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyPostsBinding.inflate(inflater, container, false);  // ✅ ĐÃ SỬA
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(MyPostsViewModel.class);

        setupToolbar(navController);
        setupViewPagerAndTabs();
        setupSearch();
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

    private void setupSearch() {
        EditText searchEditText = binding.searchEditText;
        ImageButton searchButton = binding.searchButton;

        // Xử lý sự kiện click nút tìm kiếm
        searchButton.setOnClickListener(v -> {
            String searchQuery = searchEditText.getText().toString().trim();
            viewModel.setSearchQuery(searchQuery);
        });

        // Xử lý sự kiện nhấn Enter trên thanh tìm kiếm
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String searchQuery = searchEditText.getText().toString().trim();
                viewModel.setSearchQuery(searchQuery);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Tránh memory leak
    }
}