package com.example.findittlu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import com.example.findittlu.data.model.ItemImage;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.repository.PostRepository;
import com.example.findittlu.data.repository.PostRepository.UploadCallbacks;
import com.example.findittlu.data.repository.PostRepository.RepositoryCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.net.Uri;
import java.util.Date;

public class EditPostViewModel extends AndroidViewModel {
    private PostRepository postRepository;
    private MutableLiveData<Post> postDetails = new MutableLiveData<>();
    private MutableLiveData<Boolean> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> imageUploadResult = new MutableLiveData<>();
    private final MutableLiveData<ImageDeleteResult> imageDeleteResult = new MutableLiveData<>();
    private Post originalPost; // Lưu trữ bài viết gốc để lấy thông tin cần thiết


    public static class ImageDeleteResult {
        private final Boolean success;
        private final Object deletedItem;
        private final String error;

        public ImageDeleteResult(Boolean success, Object deletedItem, String error) {
            this.success = success;
            this.deletedItem = deletedItem;
            this.error = error;
        }

        public Boolean getSuccess() { return success; }
        public Object getDeletedItem() { return deletedItem; }
        public String getError() { return error; }
    }


    public EditPostViewModel(Application application) {
        super(application);
        postRepository = new PostRepository(application);
    }

    public LiveData<Post> getPostDetails() {
        return postDetails;
    }

    public LiveData<Boolean> getUpdateResult() {
        return updateResult;
    }

    public LiveData<Boolean> getImageUploadResult() {
        return imageUploadResult;
    }

    public LiveData<ImageDeleteResult> getImageDeleteResult() {
        return imageDeleteResult;
    }

    public void fetchPostDetails(String postId) {
        long id = Long.parseLong(postId);
        postRepository.getPostDetails(id).observeForever(post -> {
            originalPost = post; // Lưu trữ bài viết gốc
            postDetails.setValue(post);
        });
    }

    public void savePostChanges(String postId, String title, String description, long categoryId, String location, String date) {
        long id = Long.parseLong(postId);
        
        // Tạo Map thay vì Post object để tránh vấn đề với serializer
        java.util.Map<String, Object> postData = new java.util.HashMap<>();
        postData.put("title", title);
        postData.put("description", description);
        postData.put("category_id", categoryId);
        postData.put("location_description", location);
        postData.put("is_contact_info_public", true);
        
        // Lấy item_type từ bài viết gốc
        if (originalPost != null) {
            postData.put("item_type", originalPost.getItemType());
        }
        
        try {
            // Parse date từ format dd/MM/yyyy và chuyển thành YYYY-MM-DD
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            String formattedDate = outputFormat.format(parsedDate);
            
            // Gửi date dưới dạng string với format YYYY-MM-DD
            postData.put("date_lost_or_found", formattedDate);
            
            android.util.Log.d("EditPostViewModel", "Sending date: " + formattedDate);
            android.util.Log.d("EditPostViewModel", "Sending post data: " + postData.toString());
        } catch (ParseException e) {
            android.util.Log.e("EditPostViewModel", "Error parsing date: " + e.getMessage());
            // Sử dụng ngày hiện tại nếu parse lỗi
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = outputFormat.format(new Date());
            postData.put("date_lost_or_found", currentDate);
            android.util.Log.d("EditPostViewModel", "Using current date instead: " + currentDate);
        }

        // Gọi API với Map thay vì Post object
        postRepository.updatePostWithMap(id, postData).observeForever(updatedPost -> {
            android.util.Log.d("EditPostViewModel", "Update response received:");
            android.util.Log.d("EditPostViewModel", "updatedPost: " + updatedPost);
            android.util.Log.d("EditPostViewModel", "updatedPost != null: " + (updatedPost != null));
            
            if (updatedPost != null) {
                android.util.Log.d("EditPostViewModel", "Update successful - Post ID: " + updatedPost.getId());
                android.util.Log.d("EditPostViewModel", "Update successful - Title: " + updatedPost.getTitle());
                android.util.Log.d("EditPostViewModel", "Update successful - Status: " + updatedPost.getStatus());
            } else {
                android.util.Log.e("EditPostViewModel", "Update failed - updatedPost is null");
            }
            
            updateResult.setValue(updatedPost != null);
        });
    }

    public void uploadNewImages(String postId, List<Uri> newImageUris) {
        long id = Long.parseLong(postId);
        postRepository.uploadImages(id, newImageUris, new UploadCallbacks() {
            @Override
            public void onUploadSuccess() {
                imageUploadResult.postValue(true);
            }

            @Override
            public void onUploadFailure(String message) {
                imageUploadResult.postValue(false);
            }
        });
    }

    public void deleteImage(ItemImage image) {
        postRepository.deleteImage(image.getId(), new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                imageDeleteResult.postValue(new ImageDeleteResult(true, image, null));
            }

            @Override
            public void onError(String error) {
                imageDeleteResult.postValue(new ImageDeleteResult(false, image, error));
            }
        });
    }
} 