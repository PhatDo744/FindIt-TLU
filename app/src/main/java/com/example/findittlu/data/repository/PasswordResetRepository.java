package com.example.findittlu.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findittlu.data.api.ApiService;
import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.utils.VoidApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordResetRepository {
    private final ApiService apiService;

    public PasswordResetRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public LiveData<VoidApiResponse> forgotPassword(String email) {
        MutableLiveData<VoidApiResponse> responseLiveData = new MutableLiveData<>();
        apiService.forgotPassword(email).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    responseLiveData.postValue(VoidApiResponse.success());
                } else {
                    responseLiveData.postValue(VoidApiResponse.error(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                responseLiveData.postValue(VoidApiResponse.failure(t));
            }
        });
        return responseLiveData;
    }

    public LiveData<VoidApiResponse> verifyOtp(String email, String otp) {
        MutableLiveData<VoidApiResponse> responseLiveData = new MutableLiveData<>();
        apiService.verifyOtp(email, otp).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    responseLiveData.postValue(VoidApiResponse.success());
                } else {
                    responseLiveData.postValue(VoidApiResponse.error(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                responseLiveData.postValue(VoidApiResponse.failure(t));
            }
        });
        return responseLiveData;
    }

    public LiveData<VoidApiResponse> resetPassword(String email, String otp, String password, String passwordConfirmation) {
        MutableLiveData<VoidApiResponse> responseLiveData = new MutableLiveData<>();
        apiService.resetPassword(email, otp, password, passwordConfirmation).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    responseLiveData.postValue(VoidApiResponse.success());
                } else {
                    responseLiveData.postValue(VoidApiResponse.error(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                responseLiveData.postValue(VoidApiResponse.failure(t));
            }
        });
        return responseLiveData;
    }
} 