package com.example.findittlu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findittlu.R;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {
    private List<NotificationItem> notificationList;
    private static final int TYPE_NOTIFICATION = 0;
    private static final int TYPE_FOOTER = 1;

    private OnMarkAllAsReadListener markAllAsReadListener;
    public interface OnMarkAllAsReadListener {
        void onMarkAllAsRead();
    }
    public void setOnMarkAllAsReadListener(OnMarkAllAsReadListener listener) {
        this.markAllAsReadListener = listener;
    }

    public interface OnNotificationLongClickListener {
        void onNotificationLongClick(int position, NotificationItem item);
    }
    private OnNotificationLongClickListener longClickListener;
    public void setOnNotificationLongClickListener(OnNotificationLongClickListener listener) {
        this.longClickListener = listener;
    }

    public NotificationsAdapter(List<NotificationItem> notificationList) {
        this.notificationList = notificationList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == notificationList.size()) return TYPE_FOOTER;
        return TYPE_NOTIFICATION;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_footer, parent, false);
            return new FooterViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.btnMarkAllAsRead.setOnClickListener(v -> {
                if (markAllAsReadListener != null) markAllAsReadListener.onMarkAllAsRead();
            });
            return;
        }
        NotificationItem item = notificationList.get(position);
        holder.content.setText(item.getContent());
        holder.time.setText(item.getTime());
        switch (item.getType()) {
            case NotificationItem.TYPE_SUCCESS:
                holder.icon.setImageResource(R.drawable.ic_check_circle);
                holder.icon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.primary_blue));
                break;
            case NotificationItem.TYPE_INFO:
                holder.icon.setImageResource(R.drawable.ic_check);
                holder.icon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.primary_blue));
                break;
            case NotificationItem.TYPE_WARNING:
                holder.icon.setImageResource(R.drawable.ic_warning_blue);
                break;
        }
        if (item.isRead()) {
            holder.itemView.setBackgroundResource(R.color.white);
        } else {
            holder.itemView.setBackgroundResource(R.color.primary_blue_light);
        }
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onNotificationLongClick(position, item);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size() + 1; // +1 cho footer
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView content, time;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.notification_icon);
            content = itemView.findViewById(R.id.notification_content);
            time = itemView.findViewById(R.id.notification_time);
        }
    }
    public static class FooterViewHolder extends NotificationViewHolder {
        public android.widget.Button btnMarkAllAsRead;
        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            btnMarkAllAsRead = itemView.findViewById(R.id.btnMarkAllAsRead);
        }
    }

    public void setData(List<NotificationItem> list) {
        this.notificationList = list;
    }
} 