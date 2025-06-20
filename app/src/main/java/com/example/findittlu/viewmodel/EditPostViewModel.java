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

public class EditPostViewModel extends AndroidViewModel {
    private PostRepository postRepository;
    private MutableLiveData<Post> postDetails = new MutableLiveData<>();
    private MutableLiveData<Boolean> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> imageUploadResult = new MutableLiveData<>();
    private final MutableLiveData<ImageDeleteResult> imageDeleteResult = new MutableLiveData<>();


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
            postDetails.setValue(post);
        });
    }

    public void savePostChanges(String postId, String title, String description, long categoryId, String location, String date) {
        long id = Long.parseLong(postId);
        
        Post postToUpdate = new Post();
        postToUpdate.setTitle(title);
        postToUpdate.setDescription(description);
        postToUpdate.setCategoryId(categoryId);
        postToUpdate.setLocationDescription(location);
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            postToUpdate.setDateLostOrFound(sdf.parse(date));
        } catch (ParseException e) {
            // In a real app, handle this error properly
            e.printStackTrace();
        }

        postRepository.updatePost(id, postToUpdate).observeForever(updatedPost -> {
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