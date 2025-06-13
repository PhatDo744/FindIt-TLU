package com.example.findittlu.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.findittlu.ui.fragment.PostsListFragment;

public class MyPostsViewPagerAdapter extends FragmentStateAdapter {

    public MyPostsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Tạo một Fragment mới cho mỗi tab
        // Truyền loại tab (ví dụ: "ALL", "SEARCHING") để Fragment biết cần hiển thị dữ liệu nào
        switch (position) {
            case 1:
                return PostsListFragment.newInstance("SEARCHING");
            case 2:
                return PostsListFragment.newInstance("FOUND");
            case 3:
                return PostsListFragment.newInstance("COMPLETED");
            case 0:
            default:
                return PostsListFragment.newInstance("ALL");
        }
    }

    @Override
    public int getItemCount() {
        // Trả về tổng số tab
        return 4;
    }
}
