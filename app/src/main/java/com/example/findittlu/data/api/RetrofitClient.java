package com.example.findittlu.data.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonPrimitive;
import com.example.findittlu.data.model.Post;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.findittlu.data.api.ApiService;

import okio.Buffer;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    // TODO: Thay đổi URL khi có API thực tế
    private static final String BASE_URL = "http://192.168.1.4:8000/api/"; // Localhost cho quang
    // private static final String BASE_URL = "http://10.0.2.2:8000/api/"; // Localhost cho emulator
//    private static final String BASE_URL = "http://192.168.1.5:8000/api/"; // Device IP ch phat
    // private static final String BASE_URL = "https://findit-tlu.com/api/"; // Production
    
    private static Retrofit retrofit = null;
    private static Context appContext;
    
    // Custom Date Serializer
    public static class DateSerializer implements JsonSerializer<Date> {
        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return new JsonPrimitive(sdf.format(src));
        }
    }
    
    private static String getToken() {
        if (appContext != null) {
            SharedPreferences prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            return prefs.getString("token", null);
        }
        return null;
    }
    
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Gson configuration
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .registerTypeAdapter(Date.class, new DateSerializer())
                    .registerTypeAdapter(Post.class, new Post.PostSerializer())
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
                    String token = getToken();
                    
                    // Thêm header Authorization nếu có token
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json");

                    // Chỉ thêm Content-Type cho non-multipart requests và khi có body
                    if (original.body() != null) {
                        okhttp3.MediaType mediaType = original.body().contentType();
                        if (mediaType != null && !mediaType.toString().contains("multipart")) {
                            requestBuilder.header("Content-Type", "application/json");
                        }
                    }

                    if (token != null && !token.isEmpty()) {
                        requestBuilder.header("Authorization", "Bearer " + token);
                        Log.d(TAG, "Thêm token vào request: " + token.substring(0, Math.min(20, token.length())) + "...");
                    } else {
                        Log.w(TAG, "Không có token cho request: " + original.url());
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
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(authInterceptor)
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

    private static void logRequest(Request request) {
        try {
            Log.d(TAG, "--> " + request.method() + " " + request.url());
            Log.d(TAG, "Headers: " + request.headers());

            RequestBody body = request.body();
            if (body != null) {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                Log.d(TAG, "Body: " + buffer.readUtf8());
            } else {
                Log.d(TAG, "Body: (empty)");
            }
            Log.d(TAG, "--> END " + request.method());
        } catch (Exception e) {
            Log.e(TAG, "Error logging request", e);
        }
    }

    public static ApiService getApiService() {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    init(appContext);
                    getClient();
                }
            }
        }
        return retrofit.create(ApiService.class);
    }
    
    public static void clearInstance() {
        retrofit = null;
    }
}