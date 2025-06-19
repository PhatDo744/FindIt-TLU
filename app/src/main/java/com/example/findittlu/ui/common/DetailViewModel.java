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
                    detailData.setValue(new DetailData(
                        post.getTitle(),
                        post.getDescription(),
                        post.getDateLostOrFound(),
                        post.getLocationDescription(),
                        "lost".equalsIgnoreCase(post.getItemType()),
                        post.getUser() != null ? post.getUser().getFullName() : ""
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
        public String title, description, date, location, userName;
        public boolean isLost;
        public DetailData(String title, String description, Date date, String location, boolean isLost, String userName) {
            this.title = title;
            this.description = description;
            this.date = String.valueOf(date);
            this.location = location;
            this.isLost = isLost;
            this.userName = userName;
        }
    }
} 