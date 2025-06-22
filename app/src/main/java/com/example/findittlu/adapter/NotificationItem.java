package com.example.findittlu.adapter;

public class NotificationItem {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_INFO = 1;
    public static final int TYPE_WARNING = 2;
    public static final int TYPE_DELETED = 3;

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

    // Trả về ngày dạng dd-MM-yyyy
    public String getFormattedTime() {
        if (time == null) return "";
        try {
            java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", java.util.Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date date = isoFormat.parse(time);
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            // Nếu lỗi parse, trả về chuỗi gốc
            return time;
        }
    }
} 