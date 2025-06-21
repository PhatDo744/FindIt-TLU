package com.example.findittlu.adapter;

public class NotificationItem {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_INFO = 1;
    public static final int TYPE_WARNING = 2;

    private String id;
    private int type;
    private String content;
    private String time;
    private boolean isRead;

    public NotificationItem(String id, int type, String content, String time, boolean isRead) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.time = time;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public int getType() { return type; }
    public String getContent() { return content; }
    public String getTime() { return time; }
    public boolean isRead() { return isRead; }
} 