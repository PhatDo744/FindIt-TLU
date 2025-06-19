package com.example.findittlu.ui.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.findittlu.adapter.SearchResultItem;
import com.example.findittlu.data.api.RetrofitClient;
import com.example.findittlu.data.model.Post;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {
    private final MutableLiveData<List<SearchResultItem>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isApiConnected = new MutableLiveData<>(true); // Giả lập trạng thái API

    public LiveData<List<SearchResultItem>> getSearchResults() {
        return searchResults;
    }
    public LiveData<Boolean> getIsApiConnected() {
        return isApiConnected;
    }
    // Lấy dữ liệu từ API thật
    public void fetchSearchResults(String query) {
        isApiConnected.setValue(true);
        RetrofitClient.getApiService().getAllPosts(1, 20, null, null, query).enqueue(new Callback<com.example.findittlu.data.model.PostListResponse>() {
            @Override
            public void onResponse(Call<com.example.findittlu.data.model.PostListResponse> call, Response<com.example.findittlu.data.model.PostListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<SearchResultItem> list = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    for (Post post : response.body().getData()) {
                        list.add(new SearchResultItem(
                            com.example.findittlu.R.drawable.image_placeholder_background,
                            "lost".equalsIgnoreCase(post.getItemType()),
                            post.getTitle(),
                            post.getCategory() != null ? post.getCategory().getName() : "",
                            post.getLocationDescription(),
                            post.getDateLostOrFound() != null ? sdf.format(post.getDateLostOrFound()) : ""
                        ));
                    }
                    searchResults.setValue(list);
                } else {
                    searchResults.setValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<com.example.findittlu.data.model.PostListResponse> call, Throwable t) {
                isApiConnected.setValue(false);
                searchResults.setValue(new ArrayList<>());
            }
        });
    }
} 