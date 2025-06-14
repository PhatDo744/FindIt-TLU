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

    public NotificationsAdapter(List<NotificationItem> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = notificationList.get(position);
        holder.content.setText(item.getContent());
        holder.time.setText(item.getTime());
        switch (item.getType()) {
            case NotificationItem.TYPE_SUCCESS:
                holder.icon.setImageResource(R.drawable.ic_check_circle);
                holder.icon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.primary_blue));
                holder.itemView.setBackgroundResource(R.color.primary_blue_light);
                break;
            case NotificationItem.TYPE_INFO:
                holder.icon.setImageResource(R.drawable.ic_info);
                holder.icon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.primary_blue));
                holder.itemView.setBackgroundResource(android.R.color.transparent);
                break;
            case NotificationItem.TYPE_WARNING:
                holder.icon.setImageResource(R.drawable.ic_warning);
                holder.icon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.logout_red));
                holder.itemView.setBackgroundResource(android.R.color.transparent);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
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
} 