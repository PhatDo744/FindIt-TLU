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

    public LiveData<User> updateProfile(User user) {
        return userRepository.updateProfile(user);
    }
} 