package com.example.findittlu.ui.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.adapter.SearchResultItem;
import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private final MutableLiveData<List<SearchResultItem>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isApiConnected = new MutableLiveData<>(true); // Giả lập trạng thái API

    public LiveData<List<SearchResultItem>> getSearchResults() {
        return searchResults;
    }
    public LiveData<Boolean> getIsApiConnected() {
        return isApiConnected;
    }
    // Giả lập lấy dữ liệu từ API
    public void fetchSearchResults(String query) {
        isApiConnected.setValue(true); // Đổi thành false để giả lập mất kết nối
        List<SearchResultItem> list = new ArrayList<>();
        list.add(new SearchResultItem(com.example.findittlu.R.drawable.image_placeholder_background, true, "Ví da màu đen hiệu Gucci", "Ví/Túi xách", "Thư viện T45", "15/05/2025"));
        list.add(new SearchResultItem(com.example.findittlu.R.drawable.image_placeholder_background, false, "Thẻ sinh viên Hoàng Tiến Phúc", "Đồ điện tử", "Sân bóng TLU", "16/05/2025"));
        list.add(new SearchResultItem(com.example.findittlu.R.drawable.image_placeholder_background, true, "Máy tính laptop", "Đồ điện tử", "Kí túc xá K1", "16/05/2025"));
        searchResults.setValue(list);
    }
} 