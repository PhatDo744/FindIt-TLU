package com.example.findittlu.data.repository;

import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.data.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import com.google.gson.JsonElement;
import java.lang.StringBuilder;

public class UserRepository {
    public LiveData<User> getProfile() {
        MutableLiveData<User> data = new MutableLiveData<>();
        RetrofitClient.getApiService().getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<ApiResponse<User>> updateProfile(User user) {
        MutableLiveData<ApiResponse<User>> data = new MutableLiveData<>();
        RetrofitClient.getApiService().updateProfile(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    data.setValue(new ApiResponse<>(true, response.body(), null));
                } else {
                    // Xử lý lỗi từ server
                    String errorMessage = "Cập nhật thông tin thất bại";
                    try {
                        String errorBody = response.errorBody().string();
                        Gson gson = new Gson();
                        JsonObject errorJson = gson.fromJson(errorBody, JsonObject.class);
                        
                        if (errorJson.has("message")) {
                            String serverMessage = errorJson.get("message").getAsString();
                            if (serverMessage.contains("Duplicate entry") && serverMessage.contains("phone_number_unique")) {
                                errorMessage = "Số điện thoại này đã được sử dụng bởi tài khoản khác";
                            } else if (serverMessage.contains("Số điện thoại không đúng định dạng")) {
                                errorMessage = serverMessage;
                            } else if (serverMessage.contains("Họ tên không được chứa số")) {
                                errorMessage = serverMessage;
                            } else if (serverMessage.contains("Họ tên không được chứa ký tự đặc biệt")) {
                                errorMessage = serverMessage;
                            } else if (serverMessage.contains("Họ tên không được chứa khoảng trắng liên tiếp")) {
                                errorMessage = serverMessage;
                            } else if (serverMessage.contains("Họ tên không được bắt đầu hoặc kết thúc bằng khoảng trắng")) {
                                errorMessage = serverMessage;
                            } else {
                                errorMessage = serverMessage;
                            }
                        } else if (errorJson.has("errors")) {
                            JsonObject errors = errorJson.getAsJsonObject("errors");
                            StringBuilder errorBuilder = new StringBuilder();
                            
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
                    } catch (IOException e) {
                        // Sử dụng message mặc định nếu không parse được
                    }
                    data.setValue(new ApiResponse<>(false, null, errorMessage));
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                data.setValue(new ApiResponse<>(false, null, "Lỗi kết nối mạng"));
            }
        });
        return data;
    }

    public LiveData<User> uploadAvatar(Context context, Uri avatarUri) {
        MutableLiveData<User> data = new MutableLiveData<>();
        try {
            File file = getFileFromUri(context, avatarUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse(context.getContentResolver().getType(avatarUri)), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
            RetrofitClient.getApiService().updateAvatar(body).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        data.setValue(response.body());
                    } else {
                        data.setValue(null);
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    data.setValue(null);
                }
            });
        } catch (Exception e) {
            data.setValue(null);
        }
        return data;
    }

    private File getFileFromUri(Context context, Uri uri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("avatar_upload_", ".jpg", context.getCacheDir());
        tempFile.deleteOnExit();
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
        }
        return tempFile;
    }

    // Class để wrap response với thông tin lỗi
    public static class ApiResponse<T> {
        private boolean success;
        private T data;
        private String errorMessage;

        public ApiResponse(boolean success, T data, String errorMessage) {
            this.success = success;
            this.data = data;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() { return success; }
        public T getData() { return data; }
        public String getErrorMessage() { return errorMessage; }
    }
} 