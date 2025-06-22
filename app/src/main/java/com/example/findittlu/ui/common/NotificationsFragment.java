package com.example.findittlu.ui.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.findittlu.R;
import com.example.findittlu.adapter.NotificationsAdapter;
import java.util.ArrayList;

import androidx.lifecycle.ViewModelProvider;
import com.example.findittlu.utils.CustomToast;

public class NotificationsFragment extends Fragment {
    private NotificationsViewModel notificationsViewModel;
    private NotificationsAdapter adapter;
    private RecyclerView recyclerView;

    public NotificationsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        recyclerView = view.findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        adapter.setOnMarkAllAsReadListener(() -> notificationsViewModel.markAllAsRead());
        adapter.setOnNotificationLongClickListener((position, item) -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Tùy chọn thông báo")
                .setItems(new CharSequence[]{"Đánh dấu đã đọc", "Xóa thông báo"}, (dialog, which) -> {
                    if (which == 0) {
                        // Đánh dấu đã đọc
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Đánh dấu đã đọc")
                            .setMessage("Bạn có muốn đánh dấu tin này là đã đọc không?")
                            .setPositiveButton("Đánh dấu", (d, w) -> notificationsViewModel.markAsRead(item))
                            .setNegativeButton("Hủy", null)
                            .show();
                    } else if (which == 1) {
                        // Xóa thông báo
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Xóa thông báo")
                            .setMessage("Bạn có chắc chắn muốn xóa thông báo này không?")
                            .setPositiveButton("Xóa", (d, w) -> notificationsViewModel.deleteNotification(item))
                            .setNegativeButton("Hủy", null)
                            .show();
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
        });
        adapter.setOnDeleteAllListener(() -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xóa tất cả thông báo")
                .setMessage("Bạn có chắc chắn muốn xóa tất cả thông báo không?")
                .setPositiveButton("Xóa tất cả", (dialog, which) -> notificationsViewModel.deleteAllNotifications())
                .setNegativeButton("Hủy", null)
                .show();
        });
        notificationsViewModel.getIsApiConnected().observe(getViewLifecycleOwner(), connected -> {
            if (!connected) CustomToast.showCustomToast(getContext(), "Lỗi kết nối", "Không thể kết nối API!");
        });
        notificationsViewModel.getNotifications().observe(getViewLifecycleOwner(), list -> {
            adapter.setData(list);
            adapter.notifyDataSetChanged();
            View tvNoNotifications = view.findViewById(R.id.tvNoNotifications);
            if (list == null || list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvNoNotifications.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvNoNotifications.setVisibility(View.GONE);
            }
        });
        notificationsViewModel.fetchNotifications(1, 20);
        // TODO: Xử lý Toolbar và BottomNavigationView nếu cần (nên đặt ở MainActivity)
    }
} 