package com.example.findittlu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.findittlu.data.model.LoginResponse;
import com.example.findittlu.data.repository.LoginRepository;

public class LoginViewModel extends ViewModel {
    private final LoginRepository loginRepository = new LoginRepository();

    public LiveData<LoginResponse> login(String email, String password) {
        return loginRepository.login(email, password);
    }
} 