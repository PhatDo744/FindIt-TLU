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
import com.example.findittlu.data.model.Post;
import com.example.findittlu.ui.profile.adapter.MyPostsAdapter;
import com.example.findittlu.viewmodel.MyPostsViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class PostListByTypeFragment extends Fragment implements MyPostsAdapter.OnPostActionListener {

    private static final String ARG_POST_TYPE = "post_type";
    private String postType;
    private MyPostsViewModel viewModel;
    private MyPostsAdapter adapter;
    private long userId;

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
        adapter = new MyPostsAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Lấy userId từ SharedPreferences
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);
        if (userId == -1) {
            android.widget.Toast.makeText(getContext(), "Bạn chưa đăng nhập hoặc thiếu userId!", android.widget.Toast.LENGTH_LONG).show();
            if (getActivity() != null) getActivity().onBackPressed();
            return;
        }
        
        setupObservers();
        viewModel.fetchMyPosts(userId); // Bắt đầu tải dữ liệu
    }

    private void setupObservers() {
        // Observer cho danh sách bài đăng
        viewModel.getMyPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts == null) {
                android.util.Log.e("DEBUG_MyPosts", "posts null - Lỗi API hoặc thiếu userId");
                android.widget.Toast.makeText(getContext(), "Lỗi API hoặc thiếu userId. Vui lòng kiểm tra lại!", android.widget.Toast.LENGTH_LONG).show();
                adapter.updateData(new ArrayList<>()); // Clear data on error
                return;
            }

            android.util.Log.d("DEBUG_MyPosts", "Total posts received: " + posts.size() + " for filter: " + postType);

            java.util.List<Post> filteredList = new ArrayList<>();
            switch (postType.toLowerCase()) {
                case "all":
                    filteredList.addAll(posts);
                    break;
                case "lost":
                    for (Post p : posts) {
                        if (("lost".equalsIgnoreCase(p.getItemType()) || "searching".equalsIgnoreCase(p.getStatus())) && !"completed".equalsIgnoreCase(p.getStatus()) && !"returned".equalsIgnoreCase(p.getStatus())) {
                            filteredList.add(p);
                        }
                    }
                    break;
                case "found":
                    for (Post p : posts) {
                        if ("found".equalsIgnoreCase(p.getItemType()) && !"completed".equalsIgnoreCase(p.getStatus()) && !"returned".equalsIgnoreCase(p.getStatus())) {
                            filteredList.add(p);
                        }
                    }
                    break;
                case "completed":
                    for (Post p : posts) {
                        if ("completed".equalsIgnoreCase(p.getStatus()) || "returned".equalsIgnoreCase(p.getStatus())) {
                            filteredList.add(p);
                        }
                    }
                    break;
            }
            android.util.Log.d("DEBUG_MyPosts", "Filtered posts count: " + filteredList.size());
            adapter.updateData(filteredList);
        });

        // Observer cho kết quả hoàn thành
        viewModel.getCompletionResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Snackbar.make(requireView(), "Đã đánh dấu hoàn thành!", Snackbar.LENGTH_SHORT).show();
                viewModel.fetchMyPosts(userId); // Tải lại danh sách
            } else if (success != null) {
                Snackbar.make(requireView(), "Có lỗi xảy ra, vui lòng thử lại.", Snackbar.LENGTH_SHORT).show();
            }
        });

        // Observer cho kết quả xóa
        viewModel.getDeletionResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Snackbar.make(requireView(), "Đã xóa bài đăng thành công!", Snackbar.LENGTH_SHORT).show();
                viewModel.fetchMyPosts(userId); // Tải lại danh sách
            } else if (success != null) {
                Snackbar.make(requireView(), "Có lỗi xảy ra, vui lòng thử lại.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCompleteClick(Post post) {
        viewModel.markPostAsCompleted(post.getId());
    }

    @Override
    public void onDeleteClick(Post post) {
        viewModel.deletePost(post.getId());
    }
} 
