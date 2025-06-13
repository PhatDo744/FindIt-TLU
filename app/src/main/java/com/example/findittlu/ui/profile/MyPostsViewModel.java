package com.example.findittlu.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.repository.PostRepository;
import java.util.List;

public class MyPostsViewModel extends ViewModel {
    private PostRepository postRepository;

    public MyPostsViewModel() {
        postRepository = new PostRepository();
    }

    public LiveData<List<Post>> getPostsByType(String type) {
        return postRepository.getPostsByType(type);
    }
}
