package com.example.findittlu.ui.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.adapter.NotificationItem;
import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.data.model.Notification;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsViewModel extends ViewModel {
    private final MutableLiveData<List<NotificationItem>> notifications = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isApiConnected = new MutableLiveData<>(true); // Giả lập trạng thái API

    public LiveData<List<NotificationItem>> getNotifications() {
        return notifications;
    }
    public LiveData<Boolean> getIsApiConnected() {
        return isApiConnected;
    }
    // Lấy dữ liệu từ API thật
    public void fetchNotifications(int page, int perPage) {
        isApiConnected.setValue(true);
        RetrofitClient.getApiService().getNotifications(page, perPage).enqueue(new Callback<com.example.findittlu.data.model.NotificationListResponse>() {
            @Override
            public void onResponse(Call<com.example.findittlu.data.model.NotificationListResponse> call, Response<com.example.findittlu.data.model.NotificationListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    // Chuyển đổi sang NotificationItem nếu cần
                    List<NotificationItem> list = new ArrayList<>();
                    for (Notification n : response.body().getData()) {
                        list.add(new NotificationItem(NotificationItem.TYPE_SUCCESS, n.getData().getTitle(), n.getCreatedAt()));
                    }
                    notifications.setValue(list);
                } else {
                    notifications.setValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<com.example.findittlu.data.model.NotificationListResponse> call, Throwable t) {
                isApiConnected.setValue(false);
                notifications.setValue(new ArrayList<>());
            }
        });
    }
} 