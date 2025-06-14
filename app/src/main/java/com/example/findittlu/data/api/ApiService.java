package com.example.findittlu.data.api;

import com.example.findittlu.data.model.User;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.data.model.Category;
import com.example.findittlu.data.model.ItemImage;

import java.util.List;

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

public interface ApiService {
    // User
    @GET("user/profile")
    Call<User> getProfile();

    @PUT("user/profile")
    Call<User> updateProfile(@Body User user);

    // Post (Item)
    @GET("items")
    Call<List<Post>> getMyPosts(@Query("user_id") long userId);

    @POST("items")
    Call<Post> createPost(@Body Post post);

    @PUT("items/{id}")
    Call<Post> updatePost(@Path("id") long id, @Body Post post);

    @DELETE("items/{id}")
    Call<Void> deletePost(@Path("id") long id);

    // Category
    @GET("categories")
    Call<List<Category>> getCategories();

    // Image upload
    @Multipart
    @POST("item-images")
    Call<ItemImage> uploadImage(@Part MultipartBody.Part image, @Part("item_id") RequestBody itemId);

    @FormUrlEncoded
    @POST("login")
    Call<com.example.findittlu.data.model.LoginResponse> login(
        @Field("email") String email,
        @Field("password") String password
    );
}
