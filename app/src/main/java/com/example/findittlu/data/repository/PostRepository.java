package com.example.findittlu.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PostRepository {
    private List<Post> allPosts;
    public static final boolean USE_REAL_API = false; // true: gọi API, false: giả lập

    public PostRepository() {
        // Khởi tạo dữ liệu mẫu
        createDummyData();
    }

    // Giả lập việc lấy dữ liệu từ API hoặc DB
    public LiveData<List<Post>> getPosts() {
        MutableLiveData<List<Post>> data = new MutableLiveData<>();
        // Lọc bỏ các bài đăng đã hết hạn
        List<Post> activePosts = allPosts.stream()
                                        .filter(p -> !p.isExpired())
                                        .collect(Collectors.toList());
        data.setValue(activePosts);
        return data;
    }

    public LiveData<List<Post>> getPostsByType(String type) {
        MutableLiveData<List<Post>> data = new MutableLiveData<>();
        List<Post> filteredPosts;
        
        if (type.equals("ALL")) {
            filteredPosts = allPosts.stream()
                                   .filter(p -> !p.isExpired())
                                   .collect(Collectors.toList());
        } else {
            filteredPosts = allPosts.stream()
                                   .filter(p -> p.getStatus().equalsIgnoreCase(type) && !p.isExpired())
                                   .collect(Collectors.toList());
        }
        data.setValue(filteredPosts);
        return data;
    }

    private void createDummyData() {
        allPosts = new ArrayList<>();
        
        // Tạo các bài đăng mẫu với ngày hết hạn khác nhau
        Calendar cal = Calendar.getInstance();
        
        // Bài đăng còn hạn
        Post post1 = new Post();
        post1.setTitle("Mất thẻ sinh viên khu giảng đường A2");
        post1.setStatus("SEARCHING");
        post1.setDateLostOrFound(new Date());
        post1.setCreatedAt(new Date());
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 10); // Còn 10 ngày
        post1.setExpirationDate(cal.getTime());
        allPosts.add(post1);
        
        // Bài đăng sắp hết hạn (còn 1 ngày)
        Post post2 = new Post();
        post2.setTitle("Nhặt được tai nghe Bluetooth màu trắng ở thư viện");
        post2.setStatus("FOUND");
        post2.setDateLostOrFound(new Date());
        post2.setCreatedAt(new Date());
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 1); // Còn 1 ngày
        post2.setExpirationDate(cal.getTime());
        allPosts.add(post2);
        
        // Bài đăng đã hết hạn (không hiển thị)
        Post post3 = new Post();
        post3.setTitle("Tìm thấy chìa khóa xe máy gần nhà xe C1");
        post3.setStatus("FOUND");
        post3.setDateLostOrFound(new Date());
        post3.setCreatedAt(new Date());
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -1); // Đã hết hạn
        post3.setExpirationDate(cal.getTime());
        allPosts.add(post3);
        
        // Bài đăng hoàn thành còn hạn
        Post post4 = new Post();
        post4.setTitle("Đã tìm lại được ví ở căng tin");
        post4.setStatus("COMPLETED");
        post4.setDateLostOrFound(new Date());
        post4.setCreatedAt(new Date());
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 5); // Còn 5 ngày
        post4.setExpirationDate(cal.getTime());
        allPosts.add(post4);
        
        // Bài đăng mới
        Post post5 = new Post();
        post5.setTitle("Mất sách Giải tích 1 tại phòng tự học B101");
        post5.setStatus("SEARCHING");
        post5.setDateLostOrFound(new Date());
        post5.setCreatedAt(new Date());
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 14); // Còn 14 ngày (mới tạo)
        post5.setExpirationDate(cal.getTime());
        allPosts.add(post5);
    }

    // Lấy danh sách tin đăng của user từ API hoặc giả lập
    public LiveData<List<Post>> getPostsFromApi(long userId) {
        MutableLiveData<List<Post>> data = new MutableLiveData<>();
        if (USE_REAL_API) {
            RetrofitClient.getApiService().getMyPosts(userId).enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Lọc bỏ các bài đăng đã hết hạn từ API
                        List<Post> activePosts = response.body().stream()
                                                               .filter(p -> !p.isExpired())
                                                               .collect(Collectors.toList());
                        data.setValue(activePosts);
                    }
                }
                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    data.setValue(null);
                }
            });
        } else {
            // Giả lập: lọc dummy data theo userId (nếu cần) và lọc bài hết hạn
            List<Post> userPosts = allPosts.stream()
                                          .filter(p -> !p.isExpired())
                                          .collect(Collectors.toList());
            data.setValue(userPosts);
        }
        return data;
    }

    // Tạo mới tin đăng
    public LiveData<Post> createPost(Post post) {
        MutableLiveData<Post> data = new MutableLiveData<>();
        
        // Đảm bảo set expiration date nếu chưa có
        if (post.getExpirationDate() == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 14);
            post.setExpirationDate(cal.getTime());
        }
        
        if (USE_REAL_API) {
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
        } else {
            // Giả lập: thêm vào danh sách và trả về
            allPosts.add(post);
            data.setValue(post);
        }
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
