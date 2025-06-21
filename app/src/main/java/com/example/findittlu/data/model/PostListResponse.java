package com.example.findittlu.data.model;
import java.util.List;

public class PostListResponse {
    private List<Post> data;
    // Có thể thêm trường meta nếu cần

    public List<Post> getData() { return data; }
    public void setData(List<Post> data) { this.data = data; }
} 