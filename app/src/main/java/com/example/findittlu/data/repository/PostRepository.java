package com.example.findittlu.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostRepository {
    private List<Post> allPosts;

    public PostRepository() {
        // Khởi tạo dữ liệu mẫu
        createDummyData();
    }

    // Giả lập việc lấy dữ liệu từ API hoặc DB
    public LiveData<List<Post>> getPosts() {
        MutableLiveData<List<Post>> data = new MutableLiveData<>();
        data.setValue(allPosts);
        return data;
    }

    public LiveData<List<Post>> getPostsByType(String type) {
        MutableLiveData<List<Post>> data = new MutableLiveData<>();
        if (type.equals("ALL")) {
            data.setValue(allPosts);
        } else {
            List<Post> filteredPosts = allPosts.stream()
                                               .filter(p -> p.getStatus().equalsIgnoreCase(type))
                                               .collect(Collectors.toList());
            data.setValue(filteredPosts);
        }
        return data;
    }

    private void createDummyData() {
        allPosts = new ArrayList<>();
        allPosts.add(new Post("Mất thẻ sinh viên khu giảng đường A2", "Ngày đăng: 22/05/2025", "SEARCHING"));
        allPosts.add(new Post("Nhặt được tai nghe Bluetooth màu trắng ở thư viện", "Ngày đăng: 21/05/2025", "FOUND"));
        allPosts.add(new Post("Tìm thấy chìa khóa xe máy gần nhà xe C1", "Ngày đăng: 20/05/2025", "FOUND"));
        allPosts.add(new Post("Đã tìm lại được ví ở căng tin", "Ngày đăng: 19/05/2025", "COMPLETED"));
        allPosts.add(new Post("Mất sách Giải tích 1 tại phòng tự học B101", "Ngày đăng: 18/05/2025", "SEARCHING"));
    }

    // Lấy danh sách tin đăng của user từ API
    public LiveData<List<Post>> getPostsFromApi(long userId) {
        MutableLiveData<List<Post>> data = new MutableLiveData<>();
        RetrofitClient.getApiService().getMyPosts(userId).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    // Tạo mới tin đăng
    public LiveData<Post> createPost(Post post) {
        MutableLiveData<Post> data = new MutableLiveData<>();
        RetrofitClient.getApiService().createPost(post).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    // Sửa tin đăng
    public LiveData<Post> updatePost(long id, Post post) {
        MutableLiveData<Post> data = new MutableLiveData<>();
        RetrofitClient.getApiService().updatePost(id, post).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    // Xóa tin đăng
    public LiveData<Boolean> deletePost(long id) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        RetrofitClient.getApiService().deletePost(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                data.setValue(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                data.setValue(false);
            }
        });
        return data;
    }
}
