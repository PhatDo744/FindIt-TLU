package com.example.findittlu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.repository.PostRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreatePostViewModel extends ViewModel {
    private PostRepository postRepository;
    private MutableLiveData<Boolean> creationResult = new MutableLiveData<>();

    public CreatePostViewModel() {
        this.postRepository = new PostRepository();
    }

    public void createPost(String title, String date, String status, String description, String location, int categoryId, boolean isContactInfoPublic) {
        if (title.isEmpty() || date.isEmpty() || description.isEmpty() || location.isEmpty() || categoryId == 0) {
            creationResult.setValue(false);
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
        creationResult.setValue(true);
    }

    public LiveData<Boolean> getCreationResult() {
        return creationResult;
    }
} 