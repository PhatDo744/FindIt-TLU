package com.example.findittlu.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.data.model.LoginResponse;
import com.example.findittlu.data.repository.RegisterRepository;

public class RegisterViewModel extends ViewModel {
    private final RegisterRepository registerRepository = new RegisterRepository();

    public LiveData<LoginResponse> register(String name, String email, String password, String passwordConfirmation, String phoneNumber) {
        return registerRepository.register(name, email, password, passwordConfirmation, phoneNumber);
    }
} 