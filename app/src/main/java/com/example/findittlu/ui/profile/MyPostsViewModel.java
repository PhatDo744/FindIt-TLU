package com.example.findittlu.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.repository.PostRepository;
import java.util.List;

public class MyPostsViewModel extends ViewModel {
    private final PostRepository postRepository = new PostRepository();

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
}
