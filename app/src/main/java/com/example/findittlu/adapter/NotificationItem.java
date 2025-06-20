package com.example.findittlu.adapter;

public class NotificationItem {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_INFO = 1;
    public static final int TYPE_WARNING = 2;

    private int type;
    private String content;
    private String time;

    public NotificationItem(int type, String content, String time) {
        this.type = type;
        this.content = content;
        this.time = time;
    }

    public int getType() { return type; }
    public String getContent() { return content; }
    public String getTime() { return time; }
} 