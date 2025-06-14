package com.example.findittlu.ui.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailViewModel extends ViewModel {
    private final MutableLiveData<DetailData> detailData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isApiConnected = new MutableLiveData<>(false); // Giả lập trạng thái API

    public LiveData<DetailData> getDetailData() {
        return detailData;
    }
    public LiveData<Boolean> getIsApiConnected() {
        return isApiConnected;
    }
    // Giả lập lấy dữ liệu từ API
    public void fetchDetail(String id) {
        isApiConnected.setValue(true); // Đổi thành false để giả lập mất kết nối
        // Giả lập dữ liệu
        detailData.setValue(new DetailData("Tiêu đề mẫu", "Mô tả mẫu", "15/05/2025", "Thư viện T45", true, "Nguyễn Văn A"));
    }
    public static class DetailData {
        public String title, description, date, location, userName;
        public boolean isLost;
        public DetailData(String title, String description, String date, String location, boolean isLost, String userName) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.location = location;
            this.isLost = isLost;
            this.userName = userName;
        }
    }
} 