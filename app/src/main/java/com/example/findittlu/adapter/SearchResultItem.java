package com.example.findittlu.adapter;

public class SearchResultItem {
    private int imageRes;
    private boolean isLost; // true: Đã mất, false: Đã tìm thấy
    private String title;
    private String category;
    private String location;
    private String date;

    public SearchResultItem(int imageRes, boolean isLost, String title, String category, String location, String date) {
        this.imageRes = imageRes;
        this.isLost = isLost;
        this.title = title;
        this.category = category;
        this.location = location;
        this.date = date;
    }

    public int getImageRes() { return imageRes; }
    public boolean isLost() { return isLost; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
} 