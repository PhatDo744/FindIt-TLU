package com.example.findittlu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.repository.PostRepository;
import java.util.List;

public class MyPostsViewModel extends AndroidViewModel {
    private final PostRepository postRepository;
    private final MutableLiveData<Boolean> deletionResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> completionResult = new MutableLiveData<>();
    private final MutableLiveData<List<Post>> myPosts = new MutableLiveData<>();

    public MyPostsViewModel(Application application) {
        super(application);
        postRepository = new PostRepository(application);
    }

    // LiveData cho kết quả xóa
    public LiveData<Boolean> getDeletionResult() {
        return deletionResult;
    }

    // LiveData cho kết quả hoàn thành
    public LiveData<Boolean> getCompletionResult() {
        return completionResult;
    }

    // LiveData cho danh sách bài đăng
    public LiveData<List<Post>> getMyPosts() {
        return myPosts;
    }

    public void fetchMyPosts(long userId) {
        postRepository.getPostsFromApi(userId).observeForever(posts -> {
            myPosts.setValue(posts);
        });
    }

    public void deletePost(long id) {
        postRepository.deletePost(id).observeForever(success -> {
            deletionResult.setValue(success);
        });
    }

    public void markPostAsCompleted(long id) {
        postRepository.markAsCompleted(id).observeForever(success -> {
            completionResult.setValue(success);
        });
    }
} 