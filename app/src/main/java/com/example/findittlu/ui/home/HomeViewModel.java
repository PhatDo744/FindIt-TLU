package com.example.findittlu.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.repository.PostRepository;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private PostRepository postRepository;
    private LiveData<List<Post>> posts;

    public HomeViewModel() {
        postRepository = new PostRepository();
        posts = postRepository.getPosts();
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }
} 