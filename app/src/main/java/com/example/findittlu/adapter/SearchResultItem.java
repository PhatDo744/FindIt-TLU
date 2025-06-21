package com.example.findittlu.adapter;

public class SearchResultItem {
    private long postId;
    private String imageUrl;
    private boolean isLost; // true: Đã mất, false: Đã tìm thấy
    private String title;
    private String category;
    private String location;
    private String date;

    public SearchResultItem(long postId, String imageUrl, boolean isLost, String title, String category, String location, String date) {
        this.postId = postId;
        this.imageUrl = imageUrl;
        this.isLost = isLost;
        this.title = title;
        this.category = category;
        this.location = location;
        this.date = date;
    }

    public long getPostId() { return postId; }
    public String getImageUrl() { return imageUrl; }
    public boolean isLost() { return isLost; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
} 