package com.example.findittlu.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRepository {
    public LiveData<LoginResponse> register(String name, String email, String password, String passwordConfirmation, String phoneNumber) {
        MutableLiveData<LoginResponse> data = new MutableLiveData<>();
        RetrofitClient.getApiService().register(name, email, password, passwordConfirmation, phoneNumber).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
} 