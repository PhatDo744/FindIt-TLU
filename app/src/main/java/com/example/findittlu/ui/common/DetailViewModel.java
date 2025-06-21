package com.example.findittlu.ui.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.data.model.Post;
import com.example.findittlu.utils.Constants;
import com.example.findittlu.utils.UrlUtils;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailViewModel extends ViewModel {
    private final MutableLiveData<DetailData> detailData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isApiConnected = new MutableLiveData<>(false); // Giả lập trạng thái API

    public LiveData<DetailData> getDetailData() {
        return detailData;
    }
    public LiveData<Boolean> getIsApiConnected() {
        return isApiConnected;
    }
    // Lấy dữ liệu từ API thật
    public void fetchDetail(long id) {
        isApiConnected.setValue(true);
        RetrofitClient.getApiService().getPost(id).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Post post = response.body();
                    String imageUrl = null;
                    java.util.List<String> imageUrls = new java.util.ArrayList<>();
                    
                    if (post.getImages() != null && !post.getImages().isEmpty()) {
                        // Sử dụng UrlUtils cho imageUrl đầu tiên
                        String firstImageUrl = post.getImages().get(0).getImageUrl();
                        imageUrl = firstImageUrl != null ? UrlUtils.toAbsoluteImageUrl(firstImageUrl) : null;
                        
                        for (com.example.findittlu.data.model.ItemImage img : post.getImages()) {
                            String url = img.getImageUrl();
                            if (url != null) {
                                // Sử dụng UrlUtils để xử lý URL
                                String absoluteUrl = UrlUtils.toAbsoluteImageUrl(url);
                                if (absoluteUrl != null) {
                                    imageUrls.add(absoluteUrl);
                                }
                            }
                        }
                    }
                    String userEmail = post.getUser() != null ? post.getUser().getEmail() : "";
                    String userPhone = post.getUser() != null ? post.getUser().getPhoneNumber() : "";
                    String userAvatarUrl = post.getUser() != null ? post.getUser().getPhotoUrl() : "";
                    String categoryName = post.getCategory() != null ? post.getCategory().getName() : "";

                    // Sử dụng UrlUtils để xử lý URL avatar user
                    if (userAvatarUrl != null && !userAvatarUrl.isEmpty()) {
                        userAvatarUrl = UrlUtils.toAbsoluteAvatarUrl(userAvatarUrl);
                    }

                    detailData.setValue(new DetailData(
                        post.getTitle(),
                        post.getDescription(),
                        post.getDateLostOrFound(),
                        post.getLocationDescription(),
                        "lost".equalsIgnoreCase(post.getItemType()),
                        post.getUser() != null ? post.getUser().getFullName() : "",
                        imageUrl,
                        userEmail,
                        userPhone,
                        userAvatarUrl,
                        imageUrls,
                        categoryName
                    ));
                } else {
                    detailData.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                isApiConnected.setValue(false);
                detailData.setValue(null);
            }
        });
    }
    public static class DetailData {
        public String title, description, date, timeAgo, location, userName, imageUrl, userEmail, userPhone, userAvatarUrl, categoryName;
        public boolean isLost;
        public java.util.List<String> imageUrls;
        public DetailData(String title, String description, Date dateObj, String location, boolean isLost, String userName, String imageUrl, String userEmail, String userPhone, String userAvatarUrl, java.util.List<String> imageUrls, String categoryName) {
            this.title = title;
            this.description = description;
            // Định dạng ngày
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            this.date = dateObj != null ? sdf.format(dateObj) : "";
            // Tính time ago
            this.timeAgo = getTimeAgo(dateObj);
            this.location = location;
            this.isLost = isLost;
            this.userName = userName;
            this.imageUrl = imageUrl;
            this.userEmail = userEmail;
            this.userPhone = userPhone;
            this.userAvatarUrl = userAvatarUrl;
            this.imageUrls = imageUrls;
            this.categoryName = categoryName;
        }
        private String getTimeAgo(Date dateObj) {
            if (dateObj == null) return "";
            long diff = new Date().getTime() - dateObj.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            if (days > 0) return days + " ngày trước";
            if (hours > 0) return hours + " giờ trước";
            if (minutes > 0) return minutes + " phút trước";
            return "Vừa xong";
        }
    }
} 