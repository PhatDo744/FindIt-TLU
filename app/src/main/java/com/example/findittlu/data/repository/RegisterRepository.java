package com.example.findittlu.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.data.model.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
                    // Xử lý lỗi từ API
                    String errorMessage = "Đăng ký thất bại";
                    
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Gson gson = new Gson();
                            JsonObject errorJson = gson.fromJson(errorBody, JsonObject.class);
                            
                            if (errorJson.has("message")) {
                                errorMessage = errorJson.get("message").getAsString();
                            } else if (errorJson.has("errors")) {
                                JsonObject errors = errorJson.getAsJsonObject("errors");
                                StringBuilder errorBuilder = new StringBuilder();
                                
                                // Xử lý lỗi email
                                if (errors.has("email")) {
                                    JsonElement emailErrors = errors.get("email");
                                    if (emailErrors.isJsonArray()) {
                                        for (JsonElement error : emailErrors.getAsJsonArray()) {
                                            if (errorBuilder.length() > 0) errorBuilder.append("\n");
                                            errorBuilder.append(error.getAsString());
                                        }
                                    } else {
                                        errorBuilder.append(emailErrors.getAsString());
                                    }
                                }
                                
                                // Xử lý lỗi phone_number
                                if (errors.has("phone_number")) {
                                    JsonElement phoneErrors = errors.get("phone_number");
                                    if (phoneErrors.isJsonArray()) {
                                        for (JsonElement error : phoneErrors.getAsJsonArray()) {
                                            if (errorBuilder.length() > 0) errorBuilder.append("\n");
                                            errorBuilder.append(error.getAsString());
                                        }
                                    } else {
                                        errorBuilder.append(phoneErrors.getAsString());
                                    }
                                }
                                
                                // Xử lý lỗi password
                                if (errors.has("password")) {
                                    JsonElement passwordErrors = errors.get("password");
                                    if (passwordErrors.isJsonArray()) {
                                        for (JsonElement error : passwordErrors.getAsJsonArray()) {
                                            if (errorBuilder.length() > 0) errorBuilder.append("\n");
                                            errorBuilder.append(error.getAsString());
                                        }
                                    } else {
                                        errorBuilder.append(passwordErrors.getAsString());
                                    }
                                }
                                
                                // Xử lý lỗi full_name
                                if (errors.has("full_name")) {
                                    JsonElement nameErrors = errors.get("full_name");
                                    if (nameErrors.isJsonArray()) {
                                        for (JsonElement error : nameErrors.getAsJsonArray()) {
                                            if (errorBuilder.length() > 0) errorBuilder.append("\n");
                                            errorBuilder.append(error.getAsString());
                                        }
                                    } else {
                                        errorBuilder.append(nameErrors.getAsString());
                                    }
                                }
                                
                                if (errorBuilder.length() > 0) {
                                    errorMessage = errorBuilder.toString();
                                }
                            }
                        } catch (Exception e) {
                            errorMessage = "Đăng ký thất bại. Vui lòng thử lại.";
                        }
                    }
                    
                    // Tạo LoginResponse với thông báo lỗi
                    LoginResponse errorResponse = new LoginResponse();
                    errorResponse.setMessage(errorMessage);
                    data.setValue(errorResponse);
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                LoginResponse errorResponse = new LoginResponse();
                errorResponse.setMessage("Lỗi kết nối. Vui lòng kiểm tra internet và thử lại.");
                data.setValue(errorResponse);
            }
        });
        return data;
    }
} 