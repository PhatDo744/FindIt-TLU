package com.example.findittlu.data.model;

import com.google.gson.annotations.SerializedName;

public class Role {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
} 