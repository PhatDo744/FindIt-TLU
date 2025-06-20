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
import com.example.findittlu.adapter.NotificationItem;
import com.example.findittlu.adapter.NotificationsAdapter;
import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Toast;

public class NotificationsFragment extends Fragment {
    private NotificationsViewModel notificationsViewModel;

    public NotificationsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        RecyclerView recyclerView = view.findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        NotificationsAdapter adapter = new NotificationsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        notificationsViewModel.getIsApiConnected().observe(getViewLifecycleOwner(), connected -> {
            if (!connected) Toast.makeText(getContext(), "Không thể kết nối API!", Toast.LENGTH_SHORT).show();
        });
        notificationsViewModel.getNotifications().observe(getViewLifecycleOwner(), list -> {
            recyclerView.setAdapter(new NotificationsAdapter(list));
        });
        notificationsViewModel.fetchNotifications(1, 20);
        // TODO: Xử lý Toolbar và BottomNavigationView nếu cần (nên đặt ở MainActivity)
    }
} 