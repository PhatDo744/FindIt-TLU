package com.example.findittlu.utils;

import android.content.Context;
import android.util.Log;
import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.data.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import com.example.findittlu.data.model.Category;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    public static void testConnection(Context context, NetworkTestCallback callback) {
        Log.d(TAG, "Bắt đầu test kết nối network...");
        
        // Test kết nối đến API categories (endpoint public)
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Category>> call = apiService.getCategories();
        
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                Log.d(TAG, "Test response code: " + response.code());
                if (response.isSuccessful()) {
                    Log.d(TAG, "Kết nối thành công! Số categories: " + (response.body() != null ? response.body().size() : 0));
                    callback.onSuccess("Kết nối thành công! Server đang hoạt động.");
                } else {
                    Log.e(TAG, "Server trả về lỗi: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có error body";
                        Log.e(TAG, "Error body: " + errorBody);
                        callback.onError("Server trả về lỗi " + response.code() + ": " + errorBody);
                    } catch (Exception e) {
                        callback.onError("Server trả về lỗi " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                callback.onError("Không thể kết nối đến server: " + t.getMessage());
            }
        });
    }

    public interface NetworkTestCallback {
        void onSuccess(String message);
        void onError(String error);
    }
} 