package com.example.findittlu.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;

public class MyPostsViewModel extends AndroidViewModel {
    private final PostRepository postRepository;
    private final MutableLiveData<List<Post>> myPosts = new MutableLiveData<>();
    private final MediatorLiveData<List<Post>> filteredPosts = new MediatorLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    private final MutableLiveData<Boolean> deletionResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> completionResult = new MutableLiveData<>();

    public MyPostsViewModel(Application application) {
        super(application);
        postRepository = new PostRepository(application);

        filteredPosts.addSource(myPosts, posts -> filter(searchQuery.getValue(), posts));
        filteredPosts.addSource(searchQuery, query -> filter(query, myPosts.getValue()));
    }

    private void filter(String query, List<Post> posts) {
        if (posts == null) {
            filteredPosts.setValue(new ArrayList<>());
            return;
        }

        if (query == null || query.isEmpty() || query.trim().isEmpty()) {
            filteredPosts.setValue(posts);
            return;
        }

        List<Post> result = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();
        for (Post post : posts) {
            if (post.getTitle() != null && post.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                result.add(post);
            }
        }
        filteredPosts.setValue(result);
    }

    public LiveData<List<Post>> getMyPosts() {
        return filteredPosts;
    }

    public LiveData<Boolean> getCompletionResult() {
        return completionResult;
    }

    public LiveData<Boolean> getDeletionResult() {
        return deletionResult;
    }

    public void fetchMyPosts(long userId) {
        postRepository.getPostsFromApi(userId).observeForever(myPosts::setValue);
    }

    public void deletePost(long id) {
        postRepository.deletePost(id).observeForever(deletionResult::setValue);
    }

    public void markPostAsCompleted(long id) {
        postRepository.markAsCompleted(id).observeForever(completionResult::setValue);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }
} 