package com.example.findittlu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.repository.PostRepository;
import java.util.List;

public class MyPostsViewModel extends AndroidViewModel {
    private final PostRepository postRepository;

    public MyPostsViewModel(Application application) {
        super(application);
        postRepository = new PostRepository(application);
    }

    public LiveData<List<Post>> getMyPosts(long userId) {
        return postRepository.getPostsFromApi(userId);
    }

    public LiveData<Post> createPost(Post post) {
        return postRepository.createPost(post);
    }

    public LiveData<Post> updatePost(long id, Post post) {
        return postRepository.updatePost(id, post);
    }

    public LiveData<Boolean> deletePost(long id) {
        return postRepository.deletePost(id);
    }

    public LiveData<Boolean> markPostAsCompleted(long id) {
        return postRepository.markAsCompleted(id);
    }
} 