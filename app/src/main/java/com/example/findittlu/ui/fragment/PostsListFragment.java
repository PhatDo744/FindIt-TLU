package com.example.findittlu.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.findittlu.adapter.MyPostsAdapter;
import com.example.findittlu.databinding.FragmentPostsListBinding;
import com.example.findittlu.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostsListFragment extends Fragment {

    private static final String ARG_POST_TYPE = "post_type";
    private FragmentPostsListBinding binding;
    private MyPostsAdapter postsAdapter;
    private List<Post> allPosts; // Danh sách chứa tất cả bài đăng (dữ liệu mẫu)
    private String postType;

    // Phương thức factory để tạo fragment với tham số
    public static PostsListFragment newInstance(String postType) {
        PostsListFragment fragment = new PostsListFragment();
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
        // Tạo dữ liệu mẫu
        createDummyData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPostsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        filterAndDisplayPosts();
    }

    private void setupRecyclerView() {
        binding.postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Khởi tạo adapter với danh sách trống ban đầu
        postsAdapter = new MyPostsAdapter(new ArrayList<>(), getContext());
        binding.postsRecyclerView.setAdapter(postsAdapter);
    }

    private void filterAndDisplayPosts() {
        List<Post> filteredList = new ArrayList<>();
        if ("ALL".equals(postType)) {
            filteredList.addAll(allPosts);
        } else {
            for (Post post : allPosts) {
                if (postType.equals(post.getStatusType())) {
                    filteredList.add(post);
                }
            }
        }
        // Cập nhật dữ liệu cho adapter
        postsAdapter.updateData(filteredList);
    }

    private void createDummyData() {
        allPosts = new ArrayList<>();
        allPosts.add(new Post("Mất thẻ sinh viên khu giảng đường A2", "Ngày đăng: 22/05/2025", "Đang tìm", "SEARCHING"));
        allPosts.add(new Post("Nhặt được tai nghe Bluetooth màu trắng ở thư viện", "Ngày đăng: 21/05/2025", "Đang giữ", "FOUND"));
        allPosts.add(new Post("Tìm thấy chìa khóa xe máy gần nhà xe C1", "Ngày đăng: 20/05/2025", "Đang giữ", "FOUND"));
        allPosts.add(new Post("Đã tìm lại được ví ở căng tin", "Ngày đăng: 19/05/2025", "Đã xong", "COMPLETED"));
        allPosts.add(new Post("Mất sách Giải tích 1 tại phòng tự học B101", "Ngày đăng: 18/05/2025", "Đang tìm", "SEARCHING"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Tránh memory leak
        binding = null;
    }
}

