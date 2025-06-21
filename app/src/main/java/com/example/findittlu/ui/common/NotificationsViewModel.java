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
                        int type = NotificationItem.TYPE_INFO; // mặc định là chờ duyệt
                        String notifType = n.getType();
                        String dataType = n.getData() != null ? n.getData().getType() : null;
                        // Ưu tiên lấy type từ data nếu có
                        if (dataType != null) notifType = dataType;
                        if (notifType != null) {
                            if (notifType.toLowerCase().contains("approved") || notifType.toLowerCase().contains("success") || notifType.toLowerCase().contains("duyệt")) {
                                type = NotificationItem.TYPE_SUCCESS;
                            } else if (notifType.toLowerCase().contains("pending") || notifType.toLowerCase().contains("chờ")) {
                                type = NotificationItem.TYPE_INFO;
                            } else if (notifType.toLowerCase().contains("rejected") || notifType.toLowerCase().contains("từ chối") || notifType.toLowerCase().contains("warning")) {
                                type = NotificationItem.TYPE_WARNING;
                            }
                        }
                        boolean isRead = n.isRead();
                        list.add(new NotificationItem(n.getId(), type, n.getData().getTitle(), n.getCreatedAt(), isRead));
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
    // Đánh dấu tất cả thông báo là đã đọc
    public void markAllAsRead() {
        isApiConnected.setValue(true);
        RetrofitClient.getApiService().markAllNotificationsAsRead().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Sau khi đánh dấu, fetch lại danh sách thông báo
                    fetchNotifications(1, 20);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isApiConnected.setValue(false);
            }
        });
    }
    // Đánh dấu 1 thông báo là đã đọc
    public void markAsRead(NotificationItem item) {
        isApiConnected.setValue(true);
        RetrofitClient.getApiService().markNotificationAsRead(item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchNotifications(1, 20);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isApiConnected.setValue(false);
            }
        });
    }
} 