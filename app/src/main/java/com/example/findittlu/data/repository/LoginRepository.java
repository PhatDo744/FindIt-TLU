package com.example.findittlu.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.data.model.LoginResponse;
import com.example.findittlu.data.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    public static final boolean USE_REAL_LOGIN = false; // true: gọi API, false: login giả lập

    public LiveData<LoginResponse> login(String email, String password) {
        MutableLiveData<LoginResponse> data = new MutableLiveData<>();
        if (USE_REAL_LOGIN) {
            RetrofitClient.getApiService().login(email, password).enqueue(new Callback<LoginResponse>() {
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
        } else {
            // Login giả lập (test UI): chỉ cho đăng nhập thành công với đúng email và password
            if ("test@tlu.edu.vn".equals(email) && "123456".equals(password)) {
                LoginResponse fake = new LoginResponse();
                User user = new User();
                user.setId(1);
                user.setFullName("Test User");
                user.setEmail(email);
                fake.setUser(user);
                fake.setToken("FAKE_TOKEN");
                data.setValue(fake);
            } else {
                data.setValue(null);
            }
        }
        return data;
    }
} 