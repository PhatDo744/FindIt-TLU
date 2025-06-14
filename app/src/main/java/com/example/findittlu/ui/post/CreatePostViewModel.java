package com.example.findittlu.ui.post;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.repository.PostRepository;

public class CreatePostViewModel extends ViewModel {
    private PostRepository postRepository;
    private MutableLiveData<Boolean> creationResult = new MutableLiveData<>();

    public CreatePostViewModel() {
        this.postRepository = new PostRepository();
    }

    public void createPost(String title, String date, String status) {
        // Validation logic can be added here
        if (title.isEmpty() || date.isEmpty()) {
            creationResult.setValue(false);
            return;
        }
        Post newPost = new Post(title, date, status);
        postRepository.createPost(newPost);
        creationResult.setValue(true); // Assume success for now
    }

    public LiveData<Boolean> getCreationResult() {
        return creationResult;
    }
} 