package com.example.findittlu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import com.example.findittlu.adapter.LostFoundItem;
import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private PostRepository postRepository;
    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private MutableLiveData<List<LostFoundItem>> postList = new MutableLiveData<>();
    private MutableLiveData<Boolean> isApiConnected = new MutableLiveData<>(true);

    public HomeViewModel() {
        postRepository = new PostRepository();
    }

    public void fetchPosts() {
        postRepository.fetchPostsFromApi((data, success) -> {
            isApiConnected.postValue(success);
            if (success && data != null) {
                posts.postValue(data);
                // Convert sang LostFoundItem
                List<LostFoundItem> items = new ArrayList<>();
                for (Post post : data) {
                    boolean isLost = "lost".equalsIgnoreCase(post.getItemType());
                    String location = post.getLocationDescription() != null ? post.getLocationDescription() : "Không xác định";
                    String date = "";
                    if (post.getDateLostOrFound() != null) {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        date = sdf.format(post.getDateLostOrFound());
                    }
                    LostFoundItem item = new LostFoundItem(
                        post.getId(),
                        post.getTitle(),
                        location,
                        date,
                        isLost,
                        com.example.findittlu.R.drawable.image_placeholder_background
                    );
                    items.add(item);
                }
                postList.postValue(items);
            } else {
                postList.postValue(new ArrayList<>());
            }
        });
    }

    public void fetchPostsByCategory(long categoryId) {
        postRepository.getPostsByCategory(categoryId).observeForever(postsByCat -> {
            if (postsByCat != null) {
                List<LostFoundItem> items = new ArrayList<>();
                for (Post post : postsByCat) {
                    boolean isLost = "lost".equalsIgnoreCase(post.getItemType());
                    String location = post.getLocationDescription() != null ? post.getLocationDescription() : "Không xác định";
                    String date = "";
                    if (post.getDateLostOrFound() != null) {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        date = sdf.format(post.getDateLostOrFound());
                    }
                    LostFoundItem item = new LostFoundItem(
                        post.getId(),
                        post.getTitle(),
                        location,
                        date,
                        isLost,
                        com.example.findittlu.R.drawable.image_placeholder_background
                    );
                    items.add(item);
                }
                postList.postValue(items);
            } else {
                postList.postValue(new ArrayList<>());
            }
        });
    }

    public void fetchPostsByKeyword(String keyword) {
        RetrofitClient.getApiService().searchItems(keyword).enqueue(new retrofit2.Callback<com.example.findittlu.data.model.PostListResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.findittlu.data.model.PostListResponse> call, retrofit2.Response<com.example.findittlu.data.model.PostListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<LostFoundItem> items = new ArrayList<>();
                    for (Post post : response.body().getData()) {
                        boolean isLost = "lost".equalsIgnoreCase(post.getItemType());
                        String location = post.getLocationDescription() != null ? post.getLocationDescription() : "Không xác định";
                        String date = "";
                        if (post.getDateLostOrFound() != null) {
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                            date = sdf.format(post.getDateLostOrFound());
                        }
                        LostFoundItem item = new LostFoundItem(
                            post.getId(),
                            post.getTitle(),
                            location,
                            date,
                            isLost,
                            com.example.findittlu.R.drawable.image_placeholder_background
                        );
                        items.add(item);
                    }
                    postList.postValue(items);
                } else {
                    postList.postValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.example.findittlu.data.model.PostListResponse> call, Throwable t) {
                postList.postValue(new ArrayList<>());
            }
        });
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }
    
    public LiveData<List<LostFoundItem>> getPostList() {
        return postList;
    }
    
    public LiveData<Boolean> getIsApiConnected() {
        return isApiConnected;
    }
} 