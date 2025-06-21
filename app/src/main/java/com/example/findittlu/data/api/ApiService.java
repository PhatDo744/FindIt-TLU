package com.example.findittlu.data.api;

import com.example.findittlu.data.model.User;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.model.PostResponse;
import com.example.findittlu.data.model.Category;
import com.example.findittlu.data.model.ItemImage;
import com.example.findittlu.data.model.LoginResponse;
import com.example.findittlu.data.model.Notification;
import com.example.findittlu.data.model.PostListResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.PartMap;

public interface ApiService {
    // Authentication
    @FormUrlEncoded
    @POST("auth/login")
    Call<LoginResponse> login(
        @Field("email") String email,
        @Field("password") String password
    );
    
    @FormUrlEncoded
    @POST("auth/register")
    Call<LoginResponse> register(
        @Field("full_name") String fullName,
        @Field("email") String email,
        @Field("password") String password,
        @Field("password_confirmation") String passwordConfirmation,
        @Field("phone_number") String phoneNumber
    );
    
    @POST("auth/logout")
    Call<Void> logout();
    
    @FormUrlEncoded
    @POST("auth/forgot-password")
    Call<Void> forgotPassword(@Field("email") String email);
    
    @FormUrlEncoded
    @POST("auth/reset-password")
    Call<Void> resetPassword(
        @Field("email") String email,
        @Field("token") String token,
        @Field("password") String password,
        @Field("password_confirmation") String passwordConfirmation
    );

    // User Profile
    @GET("user/profile")
    Call<User> getProfile();

    @PUT("user/profile")
    Call<User> updateProfile(@Body User user);
    
    @Multipart
    @POST("user/avatar")
    Call<User> updateAvatar(@Part MultipartBody.Part avatar);

    // Posts/Items - Public endpoints
    @GET("items")
    Call<PostListResponse> getAllPosts(
        @Query("page") int page,
        @Query("per_page") int perPage,
        @Query("item_type") String itemType, // 'lost' hoặc 'found'
        @Query("category_id") Long categoryId,
        @Query("search") String search
    );
    
    @GET("items/{id}")
    Call<Post> getPost(@Path("id") long id);
    
    // Posts/Items - User specific
    @GET("user/items")
    Call<PostListResponse> getMyPosts(@Query("user_id") long userId);

    @POST("items")
    Call<PostResponse> createPost(@Body Post post);

    @Multipart
    @POST("posts")
    Call<PostResponse> createPostWithImages(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("location_description") RequestBody location,
            @Part("category_id") RequestBody categoryId,
            @Part("item_type") RequestBody itemType,
            @Part("date_lost_or_found") RequestBody date,
            @Part("is_contact_info_public") RequestBody isPublic,
            @Part List<MultipartBody.Part> images
    );

    @PUT("items/{id}")
    Call<PostResponse> updatePost(@Path("id") long id, @Body Post post);

    @PUT("items/{id}")
    Call<PostResponse> updatePostWithMap(@Path("id") long id, @Body java.util.Map<String, Object> postData);

    @DELETE("items/{id}")
    Call<Void> deletePost(@Path("id") long id);
    
    @PUT("items/{id}/complete")
    Call<PostResponse> markAsCompleted(@Path("id") long id);

    // Categories
    @GET("categories")
    Call<List<Category>> getCategories();

    // Images
    @Multipart
    @POST("items/{id}/images")
    Call<Void> uploadImage(
        @Path("id") long itemId,
        @Part MultipartBody.Part image
    );

    @DELETE("item-images/{id}")
    Call<Void> deleteImage(@Path("id") long imageId);
    
    // Notifications
    @GET("notifications")
    Call<com.example.findittlu.data.model.NotificationListResponse> getNotifications(
        @Query("page") int page,
        @Query("per_page") int perPage
    );
    
    @PUT("notifications/{id}/read")
    Call<Void> markNotificationAsRead(@Path("id") String notificationId);
    
    @PUT("notifications/read-all")
    Call<Void> markAllNotificationsAsRead();

    @GET("items/category/{categoryId}")
    Call<PostListResponse> getItemsByCategory(@Path("categoryId") long categoryId);

    @GET("items/type/{itemType}")
    Call<PostListResponse> getItemsByType(@Path("itemType") String itemType);

    @GET("items/category/{categoryId}/type/{itemType}")
    Call<PostListResponse> getItemsByCategoryAndType(@Path("categoryId") long categoryId, @Path("itemType") String itemType);

    @GET("items/search/{keyword}")
    Call<PostListResponse> searchItems(@Path("keyword") String keyword);
}
