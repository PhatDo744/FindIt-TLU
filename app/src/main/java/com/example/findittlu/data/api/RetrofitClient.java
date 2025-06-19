package com.example.findittlu.data.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    // TODO: Thay đổi URL khi có API thực tế
    private static final String BASE_URL = "http://10.0.2.2:8000/api/"; // Localhost cho emulator
    // private static final String BASE_URL = "http://192.168.1.100:8000/api/"; // IP máy local
    // private static final String BASE_URL = "https://findit-tlu.com/api/"; // Production
    
    private static Retrofit retrofit = null;
    private static Context appContext;
    
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Gson configuration
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            
            // Logging interceptor (chỉ dùng trong debug)
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> 
                Log.d(TAG, message)
            );
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Auth interceptor
            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    
                    // Lấy token từ SharedPreferences
                    String token = null;
                    if (appContext != null) {
                        SharedPreferences prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                        token = prefs.getString("token", null);
                    }
                    
                    // Thêm header Authorization nếu có token
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json");
                    
                    if (token != null && !token.isEmpty()) {
                        requestBuilder.header("Authorization", "Bearer " + token);
                    }
                    
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            };
            
            // OkHttpClient configuration
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(authInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Build Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
    
    public static void clearInstance() {
        retrofit = null;
    }
}