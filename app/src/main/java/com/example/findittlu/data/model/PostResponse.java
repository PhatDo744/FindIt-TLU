package com.example.findittlu.data.model;

import com.google.gson.annotations.SerializedName;

public class PostResponse {
    @SerializedName("post")
    private Post post;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
} 