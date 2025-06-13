package com.example.findittlu.ui.common.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.findittlu.ui.profile.PostListByTypeFragment;

// Cần đảm bảo đường dẫn này đúng sau khi di chuyển PostsListFragment
// Hiện tại nó không còn tồn tại, chúng ta sẽ cần tạo một fragment tương tự
// import com.example.findittlu.ui.fragment.PostsListFragment; 

public class MyPostsViewPagerAdapter extends FragmentStateAdapter {

    public MyPostsViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return PostListByTypeFragment.newInstance("SEARCHING");
            case 2:
                return PostListByTypeFragment.newInstance("FOUND");
            case 3:
                return PostListByTypeFragment.newInstance("COMPLETED");
            case 0:
            default:
                return PostListByTypeFragment.newInstance("ALL");
        }
    }

    @Override
    public int getItemCount() {
        // Trả về tổng số tab
        return 4;
    }
}
