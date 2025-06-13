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
import com.example.findittlu.ui.common.adapter.MyPostsAdapter;

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

        viewModel.getPostsByType(postType).observe(getViewLifecycleOwner(), posts -> {
            adapter.updateData(posts);
        });
    }
} 