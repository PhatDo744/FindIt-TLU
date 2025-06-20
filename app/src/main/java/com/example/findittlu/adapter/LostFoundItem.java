package com.example.findittlu.adapter;

public class LostFoundItem {
    private String title;
    private String location;
    private String date;
    private boolean isLost; // true if lost, false if found
    private int imageUrl; // For simplicity, using a drawable resource ID
    private long postId;

    public LostFoundItem(long postId, String title, String location, String date, boolean isLost, int imageUrl) {
        this.postId = postId;
        this.title = title;
        this.location = location;
        this.date = date;
        this.isLost = isLost;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public boolean isLost() {
        return isLost;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public long getPostId() {
        return postId;
    }
} 