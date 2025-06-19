package com.example.findittlu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.adapter.LostFoundItem;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private PostRepository postRepository;
    private LiveData<List<Post>> posts;
    private LiveData<List<LostFoundItem>> postList;
    private MutableLiveData<Boolean> isApiConnected = new MutableLiveData<>(true);

    public HomeViewModel() {
        postRepository = new PostRepository();
        posts = postRepository.getPosts();
        
        // Transform Post objects to LostFoundItem objects
        postList = Transformations.map(posts, postList -> {
            List<LostFoundItem> items = new ArrayList<>();
            if (postList != null) {
                for (Post post : postList) {
                    // Convert Post to LostFoundItem
                    boolean isLost = "SEARCHING".equalsIgnoreCase(post.getStatus());
                    String location = post.getLocationDescription() != null ? 
                        post.getLocationDescription() : "Không xác định";
                    
                    LostFoundItem item = new LostFoundItem(
                        post.getTitle(),
                        location,
                        post.getDate(),
                        isLost,
                        com.example.findittlu.R.drawable.image_placeholder_background
                    );
                    items.add(item);
                }
            }
            return items;
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
    
    // Phương thức này sẽ gọi repository để lấy dữ liệu thực
    public void fetchPosts() {
        // Trong tương lai, có thể gọi API thực tại đây
        // Hiện tại PostRepository đã tự động load dữ liệu khi khởi tạo
        isApiConnected.setValue(!PostRepository.USE_REAL_API);
    }
} 