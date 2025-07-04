package com.example.findittlu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.findittlu.data.model.User;
import com.example.findittlu.data.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository = new UserRepository();

    public LiveData<User> getProfile() {
        return userRepository.getProfile();
    }

    public LiveData<UserRepository.ApiResponse<User>> updateProfile(User user) {
        return userRepository.updateProfile(user);
    }

    public LiveData<User> uploadAvatar(android.content.Context context, android.net.Uri avatarUri) {
        return userRepository.uploadAvatar(context, avatarUri);
    }
} 