package com.example.findittlu.ui.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.adapter.NotificationItem;
import java.util.ArrayList;
import java.util.List;

public class NotificationsViewModel extends ViewModel {
    private final MutableLiveData<List<NotificationItem>> notifications = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isApiConnected = new MutableLiveData<>(true); // Giả lập trạng thái API

    public LiveData<List<NotificationItem>> getNotifications() {
        return notifications;
    }
    public LiveData<Boolean> getIsApiConnected() {
        return isApiConnected;
    }
    // Giả lập lấy dữ liệu từ API
    public void fetchNotifications() {
        isApiConnected.setValue(true); // Đổi thành false để giả lập mất kết nối
        List<NotificationItem> list = new ArrayList<>();
        list.add(new NotificationItem(NotificationItem.TYPE_SUCCESS, "Tin đăng 'Mất thẻ sinh viên khu A2' của bạn đã được duyệt.", "5 phút trước"));
        list.add(new NotificationItem(NotificationItem.TYPE_SUCCESS, "Tin 'Nhặt được tai nghe Bluetooth' đã được bạn đánh dấu hoàn thành.", "1 giờ trước"));
        list.add(new NotificationItem(NotificationItem.TYPE_INFO, "Chào mừng bạn đến với FindIt@TLU! Hãy bắt đầu tìm kiếm hoặc đăng tin.", "Hôm qua"));
        list.add(new NotificationItem(NotificationItem.TYPE_WARNING, "Tin 'Mất chia khóa phòng KTX' của bạn sắp hết hạn hiển thị. Hãy gia hạn nếu cần.", "3 ngày trước"));
        notifications.setValue(list);
    }
} 