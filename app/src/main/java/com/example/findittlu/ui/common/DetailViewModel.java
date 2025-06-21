package com.example.findittlu.ui.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.data.model.Post;

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
                    if (post.getImages() != null && !post.getImages().isEmpty()) {
                        imageUrl = post.getImages().get(0).getImageUrl();
                    }
                    detailData.setValue(new DetailData(
                        post.getTitle(),
                        post.getDescription(),
                        post.getDateLostOrFound(),
                        post.getLocationDescription(),
                        "lost".equalsIgnoreCase(post.getItemType()),
                        post.getUser() != null ? post.getUser().getFullName() : "",
                        imageUrl
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
        public String title, description, date, timeAgo, location, userName, imageUrl;
        public boolean isLost;
        public DetailData(String title, String description, Date dateObj, String location, boolean isLost, String userName, String imageUrl) {
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