package com.example.findittlu.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.model.ItemImage;
import com.example.findittlu.data.repository.PostRepository;
import com.example.findittlu.data.api.RetrofitClient;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.findittlu.utils.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class CreatePostViewModel extends AndroidViewModel {

    private final PostRepository postRepository;
    private final MutableLiveData<Resource<Post>> postCreationState = new MutableLiveData<>();

    public CreatePostViewModel(@NonNull Application application) {
        super(application);
        this.postRepository = new PostRepository(application);
    }

    public LiveData<Resource<Post>> getPostCreationState() {
        return postCreationState;
    }

    public void createPost(String title, String description, String location, long categoryId, String itemType, String date, boolean isPublic, List<Uri> imageUris) {
        postCreationState.setValue(Resource.loading(null));

        List<MultipartBody.Part> imageParts = postRepository.prepareImageParts(getApplication(), imageUris);

        postRepository.createPost(title, description, location, categoryId, itemType, date, isPublic, imageParts, new PostRepository.PostCallback() {
            @Override
            public void onSuccess(Post post) {
                postCreationState.setValue(Resource.success(post));
            }

            @Override
            public void onError(String message) {
                postCreationState.setValue(Resource.error(message, null));
            }
        });
    }

    public void createPost(String title, String date, String status, String description, String location, int categoryId, boolean isContactInfoPublic) {
        if (title.isEmpty() || date.isEmpty() || description.isEmpty() || location.isEmpty() || categoryId == 0) {
            postCreationState.postValue(Resource.error("Vui lòng điền đầy đủ thông tin", null));
            return;
        }
        Post newPost = new Post();
        newPost.setTitle(title);
        newPost.setStatus(status);
        newPost.setDescription(description);
        newPost.setLocationDescription(location);
        newPost.setCategoryId(categoryId);
        // Parse date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date dateLostOrFound = sdf.parse(date);
            newPost.setDateLostOrFound(dateLostOrFound);
        } catch (ParseException e) {
            newPost.setDateLostOrFound(new Date());
        }
        newPost.setCreatedAt(new Date());
        newPost.setUpdatedAt(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 14);
        newPost.setExpirationDate(cal.getTime());
        newPost.setContactInfoPublic(isContactInfoPublic);
        newPost.setItemType(status.equals("FOUND") ? "found" : "lost");
        postRepository.createPost(newPost);
        postCreationState.postValue(Resource.success(newPost));
    }

    public void createPostWithImages(String title, String date, String status, String description, String location, int categoryId, boolean isContactInfoPublic, List<Uri> imageUris) {
        if (title.isEmpty() || date.isEmpty() || description.isEmpty() || location.isEmpty() || categoryId == 0) {
            postCreationState.postValue(Resource.error("Vui lòng điền đầy đủ thông tin", null));
            return;
        }

        // Tạo post trước
        Post newPost = new Post();
        newPost.setTitle(title);
        newPost.setStatus("PENDING"); // Luôn là PENDING khi mới tạo
        newPost.setDescription(description);
        newPost.setLocationDescription(location);
        newPost.setCategoryId(categoryId);
        newPost.setItemType(status.equals("FOUND") ? "found" : "lost");
        newPost.setContactInfoPublic(isContactInfoPublic);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            newPost.setDateLostOrFound(sdf.parse(date));
        } catch (ParseException e) {
            newPost.setDateLostOrFound(new Date());
        }

        postRepository.createPost(newPost).observeForever(post -> {
            if (post != null && post.getId() > 0) {
                postCreationState.postValue(Resource.success(post)); // Trả về post đã tạo
                if (imageUris != null && !imageUris.isEmpty()) {
                    uploadImages(post.getId(), imageUris);
                } else {
                    // Không có ảnh, coi như upload thành công
                    postCreationState.postValue(Resource.success(post));
                }
            } else {
                postCreationState.postValue(Resource.error("Đăng bài thất bại. Vui lòng thử lại.", null));
            }
        });
    }

    private void uploadImages(long postId, List<Uri> imageUris) {
        postRepository.uploadImages(postId, imageUris, new PostRepository.UploadCallbacks() {
            @Override
            public void onUploadSuccess() {
                postCreationState.postValue(Resource.success(null));
            }

            @Override
            public void onUploadFailure(String message) {
                postCreationState.postValue(Resource.error("Đăng bài thành công nhưng tải ảnh lên thất bại: " + message, null));
            }
        });
    }

    private File uriToFile(Context context, Uri uri) {
        try {
            android.util.Log.d("UriToFile", "Bắt đầu chuyển đổi URI: " + uri);
            
            // Tạo file tạm thời với extension phù hợp
            String fileName = "image_" + System.currentTimeMillis();
            File tempFile = File.createTempFile(fileName, ".jpg", context.getCacheDir());
            
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream == null) {
                    android.util.Log.e("UriToFile", "Không thể mở input stream cho URI: " + uri);
                    return null;
                }
                
                outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytes = 0;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }
                
                outputStream.flush();
                android.util.Log.d("UriToFile", "Chuyển đổi thành công: " + tempFile.getAbsolutePath() + ", size: " + totalBytes);
                return tempFile;
                
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        android.util.Log.e("UriToFile", "Lỗi đóng input stream", e);
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Exception e) {
                        android.util.Log.e("UriToFile", "Lỗi đóng output stream", e);
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.e("UriToFile", "Lỗi chuyển đổi URI thành file", e);
            return null;
        }
    }
} 