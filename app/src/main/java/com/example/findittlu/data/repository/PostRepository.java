package com.example.findittlu.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.model.PostResponse;
import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.data.model.PostListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PostRepository {
    private List<Post> allPosts;
    public static final boolean USE_REAL_API = true; // true: gọi API, false: giả lập
    private Context context;
    public PostRepository() {
        createDummyData();
    }
    public PostRepository(Context context) {
        this.context = context;
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
            RetrofitClient.getApiService().getMyPosts(userId).enqueue(new Callback<PostListResponse>() {
                @Override
                public void onResponse(Call<PostListResponse> call, Response<PostListResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        List<Post> activePosts = response.body().getData().stream()
                            .filter(p -> !p.isExpired())
                            .collect(Collectors.toList());
                        data.setValue(activePosts);
                    } else {
                        data.setValue(null);
                    }
                }
                @Override
                public void onFailure(Call<PostListResponse> call, Throwable t) {
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
        if (post.getExpirationDate() == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 14);
            post.setExpirationDate(cal.getTime());
        }
        if (USE_REAL_API) {
            android.util.Log.d("PostRepository", "Sử dụng REAL API để tạo post");
            RetrofitClient.getApiService().createPost(post).enqueue(new Callback<PostResponse>() {
                @Override
                public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                    android.util.Log.d("DEBUG_CreatePost", "onResponse: code=" + response.code() + ", body=" + response.body());
                    if (!response.isSuccessful()) {
                        try {
                            android.util.Log.e("DEBUG_CreatePost", "errorBody: " + response.errorBody().string());
                        } catch (Exception e) {
                            android.util.Log.e("DEBUG_CreatePost", "errorBody: cannot parse", e);
                        }
                    }
                    if (response.isSuccessful() && response.body() != null) {
                        data.setValue(response.body().getPost());
                    } else {
                        data.setValue(null);
                    }
                }
                @Override
                public void onFailure(Call<PostResponse> call, Throwable t) {
                    android.util.Log.e("DEBUG_CreatePost", "onFailure: " + t.getMessage(), t);
                    data.setValue(null);
                }
            });
        } else {
            android.util.Log.d("PostRepository", "Sử dụng MOCK DATA để tạo post");
            // Gán ID cho mock data
            long newId = allPosts.size() + 1;
            post.setId(newId);
            allPosts.add(post);
            android.util.Log.d("PostRepository", "Tạo post thành công với ID: " + post.getId());
            data.setValue(post);
        }
        return data;
    }

    // Lấy chi tiết một tin đăng
    public LiveData<Post> getPostDetails(long id) {
        MutableLiveData<Post> data = new MutableLiveData<>();
        RetrofitClient.getApiService().getPost(id).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
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
        
        android.util.Log.d("PostRepository", "Updating post with ID: " + id);
        android.util.Log.d("PostRepository", "Post data: title=" + post.getTitle() + 
            ", description=" + post.getDescription() + 
            ", categoryId=" + post.getCategoryId() + 
            ", location=" + post.getLocationDescription() + 
            ", itemType=" + post.getItemType() + 
            ", date=" + post.getDateLostOrFound());
        
        RetrofitClient.getApiService().updatePost(id, post).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                android.util.Log.d("PostRepository", "Update response: code=" + response.code());
                if (!response.isSuccessful()) {
                    try {
                        android.util.Log.e("PostRepository", "Update error: " + response.errorBody().string());
                    } catch (Exception e) {
                        android.util.Log.e("PostRepository", "Update error: cannot parse error body", e);
                    }
                }
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("PostRepository", "Update successful, new status: " + response.body().getPost().getStatus());
                    data.setValue(response.body().getPost());
                } else {
                    android.util.Log.e("PostRepository", "Update failed: response not successful or body null");
                    data.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                android.util.Log.e("PostRepository", "Update failure: " + t.getMessage(), t);
                data.setValue(null);
            }
        });
        return data;
    }

    // Sửa tin đăng với Map data
    public LiveData<Post> updatePostWithMap(long id, java.util.Map<String, Object> postData) {
        MutableLiveData<Post> data = new MutableLiveData<>();
        
        android.util.Log.d("PostRepository", "Updating post with Map data, ID: " + id);
        android.util.Log.d("PostRepository", "Post data: " + postData.toString());
        
        RetrofitClient.getApiService().updatePostWithMap(id, postData).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("PostRepository", "Update successful");
                    data.setValue(response.body().getPost());
                } else {
                    android.util.Log.e("PostRepository", "Update failed: " + response.code() + " " + response.message());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                android.util.Log.e("PostRepository", "Update error: " + t.getMessage());
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

    // Đánh dấu hoàn thành
    public LiveData<Boolean> markAsCompleted(long id) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        RetrofitClient.getApiService().markAsCompleted(id).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                result.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                result.setValue(false);
            }
        });
        return result;
    }

    // Xóa một ảnh của tin đăng
    public void deleteImage(long imageId, RepositoryCallback<Void> callback) {
        RetrofitClient.getApiService().deleteImage(imageId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error deleting image: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Failure deleting image: " + t.getMessage());
            }
        });
    }

    // Tải ảnh lên cho một tin đăng đã có
    public void uploadImages(long postId, List<Uri> imageUris, UploadCallbacks callbacks) {
        if (imageUris == null || imageUris.isEmpty()) {
            callbacks.onUploadSuccess();
            return;
        }
        uploadImageAtIndex(postId, imageUris, 0, callbacks);
    }

    // Upload từng ảnh một (tuần tự)
    private void uploadImageAtIndex(long postId, List<Uri> imageUris, int index, UploadCallbacks callbacks) {
        if (index >= imageUris.size()) {
            callbacks.onUploadSuccess();
            return;
        }

        Uri uri = imageUris.get(index);
        File file;
        try {
            file = getFileFromUri(uri);
        } catch (Exception e) {
            Log.e("UploadImages", "File creation failed", e);
            callbacks.onUploadFailure("Không thể tạo file từ URI.");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        RetrofitClient.getApiService().uploadImage(postId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Tải lên ảnh tiếp theo
                    uploadImageAtIndex(postId, imageUris, index + 1, callbacks);
                } else {
                    callbacks.onUploadFailure("Lỗi server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callbacks.onUploadFailure("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    private File getFileFromUri(Uri uri) throws Exception {
        if (this.context == null) {
            throw new IllegalStateException("Context is null. Make sure PostRepository is initialized with a valid Context.");
        }
        InputStream inputStream = this.context.getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("upload_", ".jpg", this.context.getCacheDir());
        tempFile.deleteOnExit();
        try (OutputStream output = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4 * 1024]; // 4k buffer
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return tempFile;
    }

    public interface UploadCallbacks {
        void onUploadSuccess();
        void onUploadFailure(String message);
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    public interface PostsCallback {
        void onResult(List<Post> data, boolean success);
    }

    public void fetchPostsFromApi(PostsCallback callback) {
        if (USE_REAL_API) {
            RetrofitClient.getApiService().getAllPosts(1, 20, null, null, null).enqueue(new Callback<PostListResponse>() {
                @Override
                public void onResponse(Call<PostListResponse> call, Response<PostListResponse> response) {
                    android.util.Log.d("DEBUG_HomeAPI", "onResponse: code=" + response.code() + ", body=" + response.body());
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        callback.onResult(response.body().getData(), true);
                    } else {
                        android.util.Log.d("DEBUG_HomeAPI", "onResponse: response not successful or body null");
                        callback.onResult(null, false);
                    }
                }
                @Override
                public void onFailure(Call<PostListResponse> call, Throwable t) {
                    android.util.Log.e("DEBUG_HomeAPI", "onFailure: " + t.getMessage(), t);
                    callback.onResult(null, false);
                }
            });
        } else {
            callback.onResult(allPosts, true);
        }
    }

    public LiveData<List<Post>> getPostsByCategory(long categoryId) {
        MutableLiveData<List<Post>> data = new MutableLiveData<>();
        RetrofitClient.getApiService().getItemsByCategory(categoryId).enqueue(new Callback<PostListResponse>() {
            @Override
            public void onResponse(Call<PostListResponse> call, Response<PostListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    data.setValue(response.body().getData());
                } else {
                    data.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<PostListResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
