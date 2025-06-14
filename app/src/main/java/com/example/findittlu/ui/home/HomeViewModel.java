package com.example.findittlu.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.adapter.LostFoundItem;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<LostFoundItem>> postList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isApiConnected = new MutableLiveData<>(true); // Giả lập trạng thái API

    public LiveData<List<LostFoundItem>> getPostList() {
        return postList;
    }
    public LiveData<Boolean> getIsApiConnected() {
        return isApiConnected;
    }
    // Giả lập lấy dữ liệu từ API
    public void fetchPosts() {
        isApiConnected.setValue(true); // Đổi thành false để giả lập mất kết nối
        List<LostFoundItem> list = new ArrayList<>();
        list.add(new LostFoundItem("Ví da màu đen hiệu Gucci", "Thư viện T45", "15/05/2025", true, com.example.findittlu.R.drawable.image_placeholder_background));
        list.add(new LostFoundItem("Thẻ sinh viên Hoàng Tiến Phúc", "Sân bóng TLU", "16/05/2025", false, com.example.findittlu.R.drawable.image_placeholder_background));
        list.add(new LostFoundItem("Laptop Dell XPS 15", "Phòng học A101", "14/05/2025", true, com.example.findittlu.R.drawable.image_placeholder_background));
        postList.setValue(list);
    }
} 