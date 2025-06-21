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
    private boolean showAllResults = false;

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
                        String imageUrl = null;
                        if (post.getImages() != null && !post.getImages().isEmpty()) {
                            imageUrl = post.getImages().get(0).getImageUrl();
                        }
                        list.add(new SearchResultItem(
                            post.getId(),
                            imageUrl,
                            "lost".equalsIgnoreCase(post.getItemType()),
                            post.getTitle(),
                            post.getCategory() != null ? post.getCategory().getName() : "",
                            post.getLocationDescription(),
                            post.getDateLostOrFound() != null ? sdf.format(post.getDateLostOrFound()) : ""
                        ));
                    }
                    if (!showAllResults && list.size() > 5) list = list.subList(0, 5);
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

    public void fetchSearchResultsByCategory(Long categoryId) {
        isApiConnected.setValue(true);
        RetrofitClient.getApiService().getItemsByCategory(categoryId).enqueue(new Callback<com.example.findittlu.data.model.PostListResponse>() {
            @Override
            public void onResponse(Call<com.example.findittlu.data.model.PostListResponse> call, Response<com.example.findittlu.data.model.PostListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<SearchResultItem> list = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    for (Post post : response.body().getData()) {
                        String imageUrl = null;
                        if (post.getImages() != null && !post.getImages().isEmpty()) {
                            imageUrl = post.getImages().get(0).getImageUrl();
                        }
                        list.add(new SearchResultItem(
                            post.getId(),
                            imageUrl,
                            "lost".equalsIgnoreCase(post.getItemType()),
                            post.getTitle(),
                            post.getCategory() != null ? post.getCategory().getName() : "",
                            post.getLocationDescription(),
                            post.getDateLostOrFound() != null ? sdf.format(post.getDateLostOrFound()) : ""
                        ));
                    }
                    if (!showAllResults && list.size() > 5) list = list.subList(0, 5);
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

    public void fetchSearchResultsByType(String itemType) {
        isApiConnected.setValue(true);
        RetrofitClient.getApiService().getItemsByType(itemType).enqueue(new Callback<com.example.findittlu.data.model.PostListResponse>() {
            @Override
            public void onResponse(Call<com.example.findittlu.data.model.PostListResponse> call, Response<com.example.findittlu.data.model.PostListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<SearchResultItem> list = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    for (Post post : response.body().getData()) {
                        String imageUrl = null;
                        if (post.getImages() != null && !post.getImages().isEmpty()) {
                            imageUrl = post.getImages().get(0).getImageUrl();
                        }
                        list.add(new SearchResultItem(
                            post.getId(),
                            imageUrl,
                            "lost".equalsIgnoreCase(post.getItemType()),
                            post.getTitle(),
                            post.getCategory() != null ? post.getCategory().getName() : "",
                            post.getLocationDescription(),
                            post.getDateLostOrFound() != null ? sdf.format(post.getDateLostOrFound()) : ""
                        ));
                    }
                    if (!showAllResults && list.size() > 5) list = list.subList(0, 5);
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

    public void fetchSearchResultsByCategoryAndType(Long categoryId, String itemType) {
        isApiConnected.setValue(true);
        RetrofitClient.getApiService().getItemsByCategoryAndType(categoryId, itemType).enqueue(new Callback<com.example.findittlu.data.model.PostListResponse>() {
            @Override
            public void onResponse(Call<com.example.findittlu.data.model.PostListResponse> call, Response<com.example.findittlu.data.model.PostListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<SearchResultItem> list = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    for (Post post : response.body().getData()) {
                        String imageUrl = null;
                        if (post.getImages() != null && !post.getImages().isEmpty()) {
                            imageUrl = post.getImages().get(0).getImageUrl();
                        }
                        list.add(new SearchResultItem(
                            post.getId(),
                            imageUrl,
                            "lost".equalsIgnoreCase(post.getItemType()),
                            post.getTitle(),
                            post.getCategory() != null ? post.getCategory().getName() : "",
                            post.getLocationDescription(),
                            post.getDateLostOrFound() != null ? sdf.format(post.getDateLostOrFound()) : ""
                        ));
                    }
                    if (!showAllResults && list.size() > 5) list = list.subList(0, 5);
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

    public void fetchSearchResultsByKeyword(String keyword) {
        isApiConnected.setValue(true);
        RetrofitClient.getApiService().searchItems(keyword).enqueue(new Callback<com.example.findittlu.data.model.PostListResponse>() {
            @Override
            public void onResponse(Call<com.example.findittlu.data.model.PostListResponse> call, Response<com.example.findittlu.data.model.PostListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<SearchResultItem> list = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    for (Post post : response.body().getData()) {
                        String imageUrl = null;
                        if (post.getImages() != null && !post.getImages().isEmpty()) {
                            imageUrl = post.getImages().get(0).getImageUrl();
                        }
                        list.add(new SearchResultItem(
                            post.getId(),
                            imageUrl,
                            "lost".equalsIgnoreCase(post.getItemType()),
                            post.getTitle(),
                            post.getCategory() != null ? post.getCategory().getName() : "",
                            post.getLocationDescription(),
                            post.getDateLostOrFound() != null ? sdf.format(post.getDateLostOrFound()) : ""
                        ));
                    }
                    if (!showAllResults && list.size() > 5) list = list.subList(0, 5);
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

    public void setShowAllResults(boolean showAll) {
        this.showAllResults = showAll;
        // Gọi lại fetchSearchResults để cập nhật danh sách
        fetchSearchResults("");
    }

    public boolean isShowAllResults() {
        return showAllResults;
    }
} 