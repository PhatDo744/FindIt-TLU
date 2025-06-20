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

    public LiveData<User> updateProfile(User user) {
        MutableLiveData<User> data = new MutableLiveData<>();
        RetrofitClient.getApiService().updateProfile(user).enqueue(new Callback<User>() {
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
} 