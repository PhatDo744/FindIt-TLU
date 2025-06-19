package com.example.findittlu.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.findittlu.R;
import com.example.findittlu.ui.profile.adapter.MyPostsAdapter;
import com.example.findittlu.viewmodel.MyPostsViewModel;

import java.util.ArrayList;

public class PostListByTypeFragment extends Fragment {

    private static final String ARG_POST_TYPE = "post_type";
    private String postType;
    private MyPostsViewModel viewModel;
    private MyPostsAdapter adapter;

    public static PostListByTypeFragment newInstance(String postType) {
        PostListByTypeFragment fragment = new PostListByTypeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_TYPE, postType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postType = getArguments().getString(ARG_POST_TYPE);
        }
        viewModel = new ViewModelProvider(this).get(MyPostsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.postsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyPostsAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Lấy userId từ SharedPreferences
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId == -1) {
            android.widget.Toast.makeText(getContext(), "Bạn chưa đăng nhập hoặc thiếu userId!", android.widget.Toast.LENGTH_LONG).show();
            if (getActivity() != null) getActivity().onBackPressed();
            return;
        }
        viewModel.getMyPosts(userId).observe(getViewLifecycleOwner(), posts -> {
            android.util.Log.d("DEBUG_MyPosts", "Số lượng posts nhận được: " + (posts == null ? "null" : posts.size()));
            android.util.Log.d("DEBUG_MyPosts", "postType filter: " + postType);
            if (posts != null) {
                for (com.example.findittlu.data.model.Post p : posts) {
                    android.util.Log.d("DEBUG_MyPosts", "Post: id=" + p.getId() + ", status=" + p.getStatus() + ", item_type=" + p.getItemType() + ", category_id=" + p.getCategoryId());
                }
                if ("ALL".equalsIgnoreCase(postType)) {
                    adapter.updateData(posts);
                } else if ("COMPLETED".equalsIgnoreCase(postType) || "returned".equalsIgnoreCase(postType)) {
                    // Chỉ hiển thị các bài đăng đã hoàn thành (status = 'returned')
                    java.util.List<com.example.findittlu.data.model.Post> filtered = new java.util.ArrayList<>();
                    for (com.example.findittlu.data.model.Post p : posts) {
                        if ("returned".equalsIgnoreCase(p.getStatus())) {
                            filtered.add(p);
                        }
                    }
                    android.util.Log.d("DEBUG_MyPosts", "Số lượng posts sau filter (returned): " + filtered.size());
                    if (filtered.isEmpty()) {
                        android.widget.Toast.makeText(getContext(), "Không có bài đăng nào đã hoàn thành!", android.widget.Toast.LENGTH_SHORT).show();
                    }
                    adapter.updateData(filtered);
                } else if ("approved".equalsIgnoreCase(postType) || "pending_approval".equalsIgnoreCase(postType) || "rejected".equalsIgnoreCase(postType) || "expired".equalsIgnoreCase(postType)) {
                    // Filter theo status khác
                    java.util.List<com.example.findittlu.data.model.Post> filtered = new java.util.ArrayList<>();
                    for (com.example.findittlu.data.model.Post p : posts) {
                        if (postType.equalsIgnoreCase(p.getStatus())) {
                            filtered.add(p);
                        }
                    }
                    android.util.Log.d("DEBUG_MyPosts", "Số lượng posts sau filter (status): " + filtered.size());
                    if (filtered.isEmpty()) {
                        android.widget.Toast.makeText(getContext(), "Không có bài đăng nào phù hợp với bộ lọc!", android.widget.Toast.LENGTH_SHORT).show();
                    }
                    adapter.updateData(filtered);
                } else if ("lost".equalsIgnoreCase(postType) || "found".equalsIgnoreCase(postType)) {
                    // Filter theo item_type
                    java.util.List<com.example.findittlu.data.model.Post> filtered = new java.util.ArrayList<>();
                    for (com.example.findittlu.data.model.Post p : posts) {
                        if (postType.equalsIgnoreCase(p.getItemType())) {
                            filtered.add(p);
                        }
                    }
                    android.util.Log.d("DEBUG_MyPosts", "Số lượng posts sau filter (item_type): " + filtered.size());
                    if (filtered.isEmpty()) {
                        android.widget.Toast.makeText(getContext(), "Không có bài đăng nào phù hợp với bộ lọc!", android.widget.Toast.LENGTH_SHORT).show();
                    }
                    adapter.updateData(filtered);
                } else {
                    // Nếu không khớp filter nào, hiển thị tất cả
                    adapter.updateData(posts);
                }
            } else {
                android.util.Log.e("DEBUG_MyPosts", "posts null - Lỗi API hoặc thiếu userId");
                android.widget.Toast.makeText(getContext(), "Lỗi API hoặc thiếu userId. Vui lòng kiểm tra lại!", android.widget.Toast.LENGTH_LONG).show();
                if (getActivity() != null) getActivity().onBackPressed();
            }
        });
    }
} 